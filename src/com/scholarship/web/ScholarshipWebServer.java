package com.scholarship.web;

import com.scholarship.model.ActionRecord;
import com.scholarship.model.StudentRecord;
import com.scholarship.service.ScholarshipService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

/**
 * Lightweight, zero-dependency Java HTTP Web Server.
 * Serves the modern UI from the ui/ directory and exposes REST JSON endpoints
 * connecting directly to the ScholarshipService coordinator (Phases 1 - 4).
 */
public class ScholarshipWebServer {

    public static final int DEFAULT_PORT = 8080;
    private final int port;
    private final ScholarshipService service;
    private final Path uiDirectory;
    private HttpServer server;

    public ScholarshipWebServer(int port, Path uiDirectory) {
        this.port = port;
        this.uiDirectory = uiDirectory;
        this.service = new ScholarshipService();
        // Load initial batch: 4 in storage (approved/review), 4 in queue (pending)
        this.service.loadSampleData(false);
        List<StudentRecord> all = service.getStudentStorage().toList();
        service.getStudentStorage().clear();
        for (int i = 0; i < all.size(); i++) {
            StudentRecord s = all.get(i);
            if (i < 4) {
                s.setStatus("Pending");
                service.getApplicationQueue().enqueue(s);
            } else {
                s.setStatus(s.getGpa() >= 8.0 ? "Approved" : "Under Review");
                service.getStudentStorage().insertSortedByMerit(s);
            }
        }
        service.syncBSTFromStorage();
    }

    public static void main(String[] args) {
        int port = DEFAULT_PORT;
        if (args.length > 0) {
            try { port = Integer.parseInt(args[0]); } catch (NumberFormatException ignored) {}
        }

        Path uiPath = Paths.get("ui").toAbsolutePath();
        if (!Files.exists(uiPath)) {
            // Check relative to project root
            uiPath = Paths.get("..", "ui").toAbsolutePath();
        }

        ScholarshipWebServer webServer = new ScholarshipWebServer(port, uiPath);
        webServer.start();
    }

    public void start() {
        int currentPort = port;
        boolean bound = false;
        while (!bound && currentPort < port + 20) {
            try {
                server = HttpServer.create(new InetSocketAddress(currentPort), 0);
                bound = true;
            } catch (IOException e) {
                currentPort++;
            }
        }

        if (!bound) {
            System.err.println("Failed to start HTTP server: no available port between " + port + " and " + (port + 19));
            return;
        }

        try {
            server.createContext("/api/status", new StatusHandler());
            server.createContext("/api/state", new StateHandler());
            server.createContext("/api/queue/process", new ProcessNextHandler());
            server.createContext("/api/undo", new UndoHandler());
            server.createContext("/api/reset", new ResetHandler());
            server.createContext("/api/apply", new ApplyHandler());
            server.createContext("/api/verify", new VerifyHandler());
            server.createContext("/api/track", new TrackHandler());
            server.createContext("/", new StaticFileHandler(uiDirectory));

            server.setExecutor(null); // Default single-thread executor
            server.start();

            System.out.println("==================================================================");
            System.out.println("  EDUSCHOLAR — NATIONAL SCHOLARSHIP & GRANT PORTAL SERVER");
            System.out.println("------------------------------------------------------------------");
            System.out.printf("  Portal Home   : http://localhost:%d%n", currentPort);
            System.out.printf("  Apply Online  : http://localhost:%d/apply.html%n", currentPort);
            System.out.printf("  Track Status  : http://localhost:%d/track.html%n", currentPort);
            System.out.printf("  Admin Portal  : http://localhost:%d/admin.html%n", currentPort);
            System.out.printf("  Static Assets : %s%n", uiDirectory);
            System.out.println("==================================================================");
            System.out.println("  Press Ctrl+C to stop the server.");

        } catch (Exception e) {
            System.err.println("Failed to initialize routes: " + e.getMessage());
        }
    }

    public void stop() {
        if (server != null) {
            server.stop(0);
        }
    }

    // =========================================================================
    // API Handlers
    // =========================================================================

    private class StatusHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendCors(exchange);
                return;
            }
            String json = "{\"status\":\"ONLINE\",\"version\":\"1.0.0\",\"phases\":\"1,2,3,4\",\"uptime\":true}";
            sendJsonResponse(exchange, 200, json);
        }
    }

    private class StateHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendCors(exchange);
                return;
            }

            try {
                List<StudentRecord> students = service.getStudentStorage().toList();
                List<StudentRecord> queue = service.getQueueRecords();
                List<ActionRecord> stack = service.getActionHistory();

                StringBuilder sb = new StringBuilder();
                sb.append("{");
                sb.append("\"activeFormula\":\"").append(escapeJson(service.getActiveFormula())).append("\",");

                // Students array
                sb.append("\"students\":[");
                for (int i = 0; i < students.size(); i++) {
                    sb.append(studentToJson(students.get(i)));
                    if (i < students.size() - 1) sb.append(",");
                }
                sb.append("],");

                // Queue array
                sb.append("\"queue\":[");
                for (int i = 0; i < queue.size(); i++) {
                    sb.append(studentToJson(queue.get(i)));
                    if (i < queue.size() - 1) sb.append(",");
                }
                sb.append("],");

                // Stack array
                sb.append("\"stack\":[");
                for (int i = 0; i < stack.size(); i++) {
                    ActionRecord a = stack.get(i);
                    sb.append("{");
                    sb.append("\"actionType\":\"").append(escapeJson(a.getActionType())).append("\",");
                    sb.append("\"studentId\":\"").append(escapeJson(a.getStudentId())).append("\",");
                    sb.append("\"description\":\"").append(escapeJson(a.getDescription())).append("\"");
                    sb.append("}");
                    if (i < stack.size() - 1) sb.append(",");
                }
                sb.append("]");

                sb.append("}");
                sendJsonResponse(exchange, 200, sb.toString());
            } catch (Exception e) {
                e.printStackTrace();
                sendJsonResponse(exchange, 500, "{\"error\":\"" + escapeJson(e.getMessage()) + "\"}");
            }
        }
    }

    private class ProcessNextHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendCors(exchange);
                return;
            }
            ScholarshipService.ProcessResult res = service.processNextApplication();
            String json = String.format("{\"success\":%b,\"message\":\"%s\"}",
                    res.success, escapeJson(res.message));
            sendJsonResponse(exchange, res.success ? 200 : 400, json);
        }
    }

    private class UndoHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendCors(exchange);
                return;
            }
            ScholarshipService.UndoResult res = service.undoLastAction();
            String json = String.format("{\"success\":%b,\"message\":\"%s\"}",
                    res.success, escapeJson(res.message));
            sendJsonResponse(exchange, res.success ? 200 : 400, json);
        }
    }

    private class ResetHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendCors(exchange);
                return;
            }
            service.getStudentStorage().clear();
            service.getApplicationQueue().clear();
            service.getActionHistoryStack().clear();
            service.loadSampleData(true);
            sendJsonResponse(exchange, 200, "{\"success\":true,\"message\":\"Dataset reloaded with 8 sample applications.\"}");
        }
    }

    private class ApplyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendCors(exchange);
                return;
            }
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendJsonResponse(exchange, 405, "{\"error\":\"Method not allowed. Use POST.\"}");
                return;
            }

            try (InputStream is = exchange.getRequestBody()) {
                String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                String name = extractJsonString(body, "name", "Student Applicant");
                double gpa = extractJsonDouble(body, "gpa", 8.0);
                double income = extractJsonDouble(body, "familyIncome", 250000.0);
                double attendance = extractJsonDouble(body, "attendancePercentage", 85.0);
                double eca = extractJsonDouble(body, "extracurricularScore", 75.0);
                String category = extractJsonString(body, "category", "Merit-cum-Means");

                // Generate new sequential Student/Application ID
                int nextNum = 1000 + service.getApplicationQueue().size() + service.getStudentStorage().size() + 1;
                String studentId = extractJsonString(body, "studentId", "STU" + nextNum);

                StudentRecord newStudent = new StudentRecord(
                        studentId, name, gpa, income, attendance, eca, category, "Pending"
                );

                service.submitApplication(newStudent);

                String json = String.format(Locale.US,
                        "{\"success\":true,\"studentId\":\"%s\",\"message\":\"Application submitted successfully!\",\"queuePosition\":%d}",
                        escapeJson(studentId), service.getApplicationQueue().size());
                sendJsonResponse(exchange, 200, json);
            } catch (Exception e) {
                sendJsonResponse(exchange, 500, "{\"error\":\"" + escapeJson(e.getMessage()) + "\"}");
            }
        }
    }

    private class VerifyHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendCors(exchange);
                return;
            }
            if (!"POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendJsonResponse(exchange, 405, "{\"error\":\"Method not allowed. Use POST.\"}");
                return;
            }

            try (InputStream is = exchange.getRequestBody()) {
                String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                String studentId = extractJsonString(body, "studentId", "");
                String newStatus = extractJsonString(body, "status", "Approved");
                String remarks = extractJsonString(body, "remarks", "Verified by Officer");

                if (studentId.isEmpty()) {
                    sendJsonResponse(exchange, 400, "{\"error\":\"Missing studentId\"}");
                    return;
                }

                // Check in Queue first
                List<StudentRecord> queueList = service.getQueueRecords();
                StudentRecord target = null;
                int queueIdx = -1;
                for (int i = 0; i < queueList.size(); i++) {
                    if (queueList.get(i).getStudentId().equalsIgnoreCase(studentId)) {
                        target = queueList.get(i);
                        queueIdx = i;
                        break;
                    }
                }

                if (target != null) {
                    // Remove from queue and rebuild
                    service.getApplicationQueue().clear();
                    for (int i = 0; i < queueList.size(); i++) {
                        if (i != queueIdx) {
                            service.getApplicationQueue().enqueue(queueList.get(i));
                        }
                    }

                    target.setStatus(newStatus);
                    service.getStudentStorage().insertSortedByMerit(target);
                    service.getStudentBST().insert(target);

                    service.getActionHistoryStack().push(new ActionRecord(
                            "VERIFY_APPLICATION",
                            target.getStudentId(),
                            String.format("Officer decision for %s: %s (%s)", target.getName(), newStatus, remarks),
                            null,
                            target
                    ));

                    String json = String.format("{\"success\":true,\"message\":\"Application for %s marked as %s.\"}",
                            escapeJson(target.getName()), escapeJson(newStatus));
                    sendJsonResponse(exchange, 200, json);
                    return;
                }

                // Check in Storage
                StudentRecord existing = service.searchStudent(studentId);
                if (existing != null) {
                    existing.setStatus(newStatus);
                    service.getActionHistoryStack().push(new ActionRecord(
                            "UPDATE_STATUS",
                            existing.getStudentId(),
                            String.format("Updated status of %s to %s", existing.getName(), newStatus),
                            null,
                            existing
                    ));
                    String json = String.format("{\"success\":true,\"message\":\"Status for %s updated to %s.\"}",
                            escapeJson(existing.getName()), escapeJson(newStatus));
                    sendJsonResponse(exchange, 200, json);
                    return;
                }

                sendJsonResponse(exchange, 404, "{\"error\":\"Student ID not found in system.\"}");
            } catch (Exception e) {
                sendJsonResponse(exchange, 500, "{\"error\":\"" + escapeJson(e.getMessage()) + "\"}");
            }
        }
    }

    private class TrackHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                sendCors(exchange);
                return;
            }

            String query = exchange.getRequestURI().getQuery();
            String id = "";
            if (query != null && query.contains("id=")) {
                for (String param : query.split("&")) {
                    String[] pair = param.split("=");
                    if (pair.length == 2 && pair[0].equalsIgnoreCase("id")) {
                        id = URLDecoder.decode(pair[1], StandardCharsets.UTF_8).trim();
                        break;
                    }
                }
            }

            if (id.isEmpty()) {
                sendJsonResponse(exchange, 400, "{\"error\":\"Missing query parameter 'id'\"}");
                return;
            }

            // Search in Queue
            for (StudentRecord s : service.getQueueRecords()) {
                if (s.getStudentId().equalsIgnoreCase(id) || s.getName().equalsIgnoreCase(id)) {
                    String json = String.format("{\"found\":true,\"inQueue\":true,\"student\":%s}", studentToJson(s));
                    sendJsonResponse(exchange, 200, json);
                    return;
                }
            }

            // Search in Storage
            StudentRecord found = service.searchStudent(id);
            if (found != null) {
                String json = String.format("{\"found\":true,\"inQueue\":false,\"student\":%s}", studentToJson(found));
                sendJsonResponse(exchange, 200, json);
                return;
            }

            sendJsonResponse(exchange, 404, "{\"found\":false,\"error\":\"Application record not found.\"}");
        }
    }

    private static String extractJsonString(String json, String key, String defaultVal) {
        if (json == null) return defaultVal;
        java.util.regex.Pattern p = java.util.regex.Pattern.compile("\"" + key + "\"\\s*:\\s*\"([^\"]*)\"");
        java.util.regex.Matcher m = p.matcher(json);
        if (m.find()) return m.group(1);
        return defaultVal;
    }

    private static double extractJsonDouble(String json, String key, double defaultVal) {
        if (json == null) return defaultVal;
        java.util.regex.Pattern p = java.util.regex.Pattern.compile("\"" + key + "\"\\s*:\\s*([0-9.]+)");
        java.util.regex.Matcher m = p.matcher(json);
        if (m.find()) {
            try { return Double.parseDouble(m.group(1)); } catch (Exception ignored) {}
        }
        return defaultVal;
    }

    // =========================================================================
    // Static File Serving Handler
    // =========================================================================

    private static class StaticFileHandler implements HttpHandler {
        private final Path baseDir;

        public StaticFileHandler(Path baseDir) {
            this.baseDir = baseDir;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String pathStr = exchange.getRequestURI().getPath();
            if (pathStr == null || pathStr.equals("/") || pathStr.isEmpty()) {
                pathStr = "/index.html";
            }

            // Prevent path traversal
            Path resolved = baseDir.resolve(pathStr.substring(1)).normalize();
            if (!resolved.startsWith(baseDir) || !Files.exists(resolved) || Files.isDirectory(resolved)) {
                String notFound = "<h1>404 Not Found</h1><p>File not found: " + pathStr + "</p>";
                exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                exchange.sendResponseHeaders(404, notFound.getBytes(StandardCharsets.UTF_8).length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(notFound.getBytes(StandardCharsets.UTF_8));
                }
                return;
            }

            String mime = getMimeType(resolved.toString());
            byte[] fileBytes = Files.readAllBytes(resolved);

            exchange.getResponseHeaders().set("Content-Type", mime);
            exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().set("Cache-Control", "no-cache, no-store, must-revalidate");
            exchange.sendResponseHeaders(200, fileBytes.length);

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(fileBytes);
            }
        }

        private String getMimeType(String path) {
            if (path.endsWith(".html")) return "text/html; charset=UTF-8";
            if (path.endsWith(".css")) return "text/css; charset=UTF-8";
            if (path.endsWith(".js")) return "application/javascript; charset=UTF-8";
            if (path.endsWith(".json")) return "application/json; charset=UTF-8";
            if (path.endsWith(".svg")) return "image/svg+xml";
            if (path.endsWith(".png")) return "image/png";
            if (path.endsWith(".ico")) return "image/x-icon";
            return "text/plain; charset=UTF-8";
        }
    }

    // =========================================================================
    // Helper Methods
    // =========================================================================

    private static void sendJsonResponse(HttpExchange exchange, int statusCode, String json) throws IOException {
        byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
        exchange.getResponseHeaders().set("Cache-Control", "no-cache, no-store, must-revalidate");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    private static void sendCors(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");
        exchange.sendResponseHeaders(204, -1);
    }

    private static String studentToJson(StudentRecord s) {
        return String.format(Locale.US,
                "{\"studentId\":\"%s\",\"name\":\"%s\",\"gpa\":%.2f,\"familyIncome\":%.1f,\"attendancePercentage\":%.1f,\"extracurricularScore\":%.1f,\"category\":\"%s\",\"status\":\"%s\",\"meritScore\":%.2f}",
                escapeJson(s.getStudentId()),
                escapeJson(s.getName()),
                s.getGpa(),
                s.getFamilyIncome(),
                s.getAttendancePercentage(),
                s.getExtracurricularScore(),
                escapeJson(s.getCategory()),
                escapeJson(s.getStatus()),
                s.getMeritScore()
        );
    }

    private static String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\b", "\\b")
                .replace("\f", "\\f")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
