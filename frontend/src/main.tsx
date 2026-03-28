import React from "react";
import ReactDOM from "react-dom/client";
import App from "@routers/App.tsx";
import { ThemeProvider } from "./context/ThemeContext";
import { RegistrationProvider } from "./context/RegistrationContext";
import "@styles/global.css";
import "@styles/components/MarkdownContent.css";
import "@config/i18n";

ReactDOM.createRoot(document.getElementById("root")!).render(
  <React.StrictMode>
    <RegistrationProvider>
      <ThemeProvider>
        <App />
      </ThemeProvider>
    </RegistrationProvider>
  </React.StrictMode>,
);
