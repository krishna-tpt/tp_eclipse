package test;

import java.net.URL;
import java.util.Arrays;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;

public class ListDb {
    public static void main(String[] args) throws Exception {
        // Ask the Odoo server for its database list (works only if listing is enabled)
        XmlRpcClientConfigImpl cfg = new XmlRpcClientConfigImpl();
        cfg.setServerURL(new URL("http://intranet.ten10.in/xmlrpc/2/db"));
        XmlRpcClient client = new XmlRpcClient();
        client.setConfig(cfg);
        Object[] dbs = (Object[]) client.execute("list", new Object[]{});
        System.out.println(Arrays.toString(dbs));
    }
}