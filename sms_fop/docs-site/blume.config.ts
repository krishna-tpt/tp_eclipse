import { defineConfig } from "blume";

export default defineConfig({
  title: "Inventory Ledger",
  description:
    "Solution documentation for the Michelin psql-inventory-integration-service — files.com/SFTP file manager, Postgres v6 schema, and DevOps runbook.",
  content: {
    root: "content",
  },
  theme: {
    accent: "teal",
    radius: "md",
    mode: "system",
    background: {
      light: "#f8fafc",
      dark: "#0f172a",
    },
    fonts: {
      display: "inter-tight",
      body: "inter",
      mono: "ibm-plex-mono",
    },
  },
});
