import { createSystem, defaultConfig } from "@chakra-ui/react";

export const system = createSystem(defaultConfig, {
  theme: {
    tokens: {
      colors: {
        principal: {
          50: { value: "#EAF1F7" },
          100: { value: "#D3E1EE" },
          200: { value: "#A9C3DB" },
          300: { value: "#7EA4C6" },
          400: { value: "#5C8AB0" },
          500: { value: "#4B759D" }, // base
          600: { value: "#3E6086" }, // hover
          700: { value: "#2F496A" },
          800: { value: "#223151" }, // dark
          900: { value: "#141C2D" },
        },

        transparente: {
          10: { value: "rgba(75, 117, 157, 0.1)" },
          20: { value: "rgba(75, 117, 157, 0.2)" },
          50: { value: "rgba(75, 117, 157, 0.5)" },
          80: { value: "rgba(75, 117, 157, 0.8)" },
        },

        secundario: {
          500: { value: "#DEAC0F" },
        },

        rojo: {
          500: { value: "#9D4B4B" },
          600: { value: "#7E3E3E" },
        },

        verde: {
          500: { value: "#4B9D79" },
          600: { value: "#3E7E62" },
        },

        background: {
          50: { value: "#F9FAFB" },
        },

        border: {
          light: { value: "#E5E7EB" },
          default: { value: "#D1D5DB" },
        },
        text: {
          header: { value: "223151" },
          body: { value: "#1E1E1E" },
          secondary: { value: "#757575" },
          button: { value: "#F5F5F5" },
        },
      },

      fonts: {
        heading: { value: "'Lilita One', cursive" },
        body: { value: "'Open Sans', sans-serif" },
      },

      fontSizes: {
        xs: { value: "12px" },
        sm: { value: "15px" },
        md: { value: "18px" },
        lg: { value: "20px" },
        xl: { value: "32px" },
      },

      shadows: {
        soft: { value: "0 2px 8px rgba(0,0,0,0.06)" },
        card: { value: "0 4px 12px rgba(0,0,0,0.08)" },
      },
    },
  },
  globalCss: {
    body: {
      bgImage: "url('/background.png')",
      bgSize: "cover",
      position: "center",
      bgRepeat: "no-repeat",
    },
    h1: {
      color: "#223151",
    },
    secondary: {
      fontFamily: "body",
      fontSize: "xs",
      color: "#757575",
    },
  },
});
