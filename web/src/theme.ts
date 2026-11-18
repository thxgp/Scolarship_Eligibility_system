import { defineTheme } from '@astryxdesign/core/theme';
import { neutralTheme } from '@astryxdesign/theme-neutral/built';

// White canvas, ink buttons, lime highlight (see DESIGN.md).
export const resultDayTheme = defineTheme({
  extends: neutralTheme,
  name: 'lime-line',
  color: { accent: '#121212', neutralStyle: 'neutral', contrast: 'high' },
  typography: {
    heading: { family: 'Space Grotesk Variable', fallbacks: 'system-ui, sans-serif', weight: 'semibold' },
    body: { family: 'Space Grotesk Variable', fallbacks: 'system-ui, sans-serif' },
  },
  tokens: {
    '--color-accent': '#121212',
    '--color-on-accent': '#ffffff',
    '--color-background-surface': '#ffffff',
    '--color-background-card': '#ffffff',
    '--color-background-muted': '#f1f1ef',
    '--color-info': '#121212',
    '--color-on-info': '#ffffff',
    '--color-info-muted': '#e6f8bd',
    '--color-on-info-muted': '#121212',
  },
  radius: { base: 8, multiplier: 1.5 },
  motion: { tempo: 1, character: 'crisp' },
});
