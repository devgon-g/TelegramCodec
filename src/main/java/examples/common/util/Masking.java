package examples.common.util;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Masking {
    public static String name(String name) {
        if(name == null) return null;
        // 한글만 (영어, 숫자 포함 이름은 제외)
        String regex = "(^[가-힣]+)$";

        Matcher matcher = Pattern.compile(regex).matcher(name);
        if(matcher.find()) {
            int length = name.length();

            String middleMask = "";
            if(length > 2) {
                middleMask = name.substring(1, length - 1);
            } else {	// 이름이 외자
                middleMask = name.substring(1, length);
            }

            String dot = "";
            for(int i = 0; i<middleMask.length(); i++) {
                dot += "*";
            }

            if(length > 2) {
                return name.substring(0, 1)
                        + middleMask.replace(middleMask, dot)
                        + name.substring(length-1, length);
            } else { // 이름이 외자 마스킹 리턴
                return name.substring(0, 1)
                        + middleMask.replace(middleMask, dot);
            }
        }
        return name;
    }

    public static String phone(String phoneNo) {
        if(phoneNo == null) return null;
        String regex = "(\\d{2,3})-?(\\d{3,4})-?(\\d{4})$";

        Matcher matcher = Pattern.compile(regex).matcher(phoneNo);
        if(matcher.find()) {
            String target = matcher.group(2);
            int length = target.length();
            char[] c = new char[length];
            Arrays.fill(c, '*');

            return phoneNo.replace(target, String.valueOf(c));
        }
        return phoneNo;
    }

    public static String email(String email) {
        if(email == null) return null;
        String regex = "\\b(\\s+)+@(\\s+.\\s+)";

        Matcher matcher = Pattern.compile(regex).matcher(email);
        if(matcher.find()) {
            String target = matcher.group(1);
            int length = target.length();
            if(length > 3) {
                char[] c = new char[length - 3];
                Arrays.fill(c, '*');

                return email.replace(target, target.substring(0, 3) + String.valueOf(c));
            }
        }
        return email;
    }
}
