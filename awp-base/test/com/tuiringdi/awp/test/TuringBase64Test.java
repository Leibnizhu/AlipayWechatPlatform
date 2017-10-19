package com.tuiringdi.awp.test;

import com.turingdi.awp.util.common.TuringBase64Util;
import org.junit.Test;

/**
 * @author Leibniz.Hu
 * Created on 2017-10-19 12:40.
 */
public class TuringBase64Test {
    @Test
    public void decodeTest() {
        String encoded = "2Iu55p2c";
        String decoded = TuringBase64Util.decode(encoded);
        System.out.println(decoded);
        encoded = "bYkL1CX3PB7sIf6YZGwYSCX3P3IjKBKjKBKMdEH0POIsIWJY1CdLIBNjoOkuHCyLIBN32Iu55p2cI3g3HtqsvGkhHts3P3kNTmigP3Q-ZGLBTO53HCL9TS5BvtM3oOkzTCdBZedzIBN31miMcAN-otj-Htqs1G6zTAN4KAVzo0dMHeipHY6gHCTLo0d5cY63HedLdBXu1minvOk6";
        decoded = TuringBase64Util.decode(encoded);
        System.out.println(decoded);
    }

    @Test
    public void encodeTest() {
        String decoded = "{\"eid\":1,\"orderId\":\"12312345678\",\"price\":1,\"name\":\"苹果\",\"callback\":\"http://dict.baidu.com\",\"success\":\"http://localhost:8083/static/page/sys/base64.html\"}";
        String encoded = TuringBase64Util.encode(decoded);
        System.out.println(encoded);
        decoded = TuringBase64Util.decode(encoded);
        System.out.println(decoded);
        System.out.println(TuringBase64Util.decode(TuringBase64Util.encode(decoded)));
        System.out.println(TuringBase64Util.encode(TuringBase64Util.decode("2Iu55p2c")));
    }
}
