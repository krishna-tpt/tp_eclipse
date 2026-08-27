package org.michelin.filemanager.tools.config;

public record ToolsConfig(
        long seed,
        DbConfig db,
        Tenants tenants,
        MasterData masterData,
        OpeningBalance openingBalance,
        Transactions transactions,
        Orders orders) {

    public record DbConfig(String url, String user, String password) {
        @Override
        public String toString() {
            return "DbConfig[url=" + url + ", user=" + user + ", password=***]";
        }
    }

    public record Tenants(int count) {}

    public record MasterData(
            int productsPerTenant,
            int warehousesPerTenant,
            int lotsPerProduct,
            int uomsPerTenant) {}

    public record OpeningBalance(
            int rows,
            String outputDir,
            String fileName,
            String csvDelimiter,
            String date) {}

    public record Transactions(
            int count,
            double inboundRatio,
            double transferPairRatio,
            boolean viaFunction) {}

    public record Orders(
            int count,
            int minLines,
            int maxLines,
            String initialState) {}
}
