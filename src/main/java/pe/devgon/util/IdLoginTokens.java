package pe.devgon.util;

import cj.tlj.app.auth.model.LoginReq;
import cj.tlj.app.auth.model.Member;
import cj.tlj.app.common.crypto.Aes256Crypto;

public class IdLoginTokens {
    private static final Aes256Crypto aes = new Aes256Crypto("aPdSgVkYp3s6v9y$B?E(H+MbQeThWmZq");

    public static String of(Member member) {
        return aes.encrypt(member.getCbntMbrId() + "|" + member.getCbntPwdNo());
    }

    public static void resolveTo(LoginReq request) {
        if ("CIDQ".equals(request.getType())) {
            String[] tokens = aes.decrypt(request.getCjfvq()).split("\\|");
            request.updateQuery(tokens[0], tokens[1]);
        } else {
            request.resolveKeyColumn();
        }
    }
}
