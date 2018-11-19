package com.rtsoftware.order.model;

import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

import java.util.ArrayList;

public class Untill {
    private static String a = "ĂẮẶẰÂẤẦẬăắằặâấầậÁÀẠáàạẲẴẳẵẨẪẩẫẢÃảã";
    private static String e = "éèẹÉÈẸÊẾỀỆêếềệẺẼẻẽỂỄểễ";
    private static String i = "íìịÍÌỊỈĨỉĩ";
    private static String o = "ÓÒỌóòọÔỐỒỘôốồộƠỚỜỢơớờợỎÕỏõỔỖổỗỞỖởỡ";
    private static String u = "ÚÙỤúùụƯỨỪỰưứừựỦŨủũỬỮửữ";
    private static String y = "ÝỲỴýỳỵỶỸỷỹ";
    private static String d = "Đđ";

    public Untill() {
    }

    public String convertStringUTF8(String str) {
        String tmp = "";
        for (int i = 0; i < str.length(); i++) {
            tmp += convertCharUTF8(str.charAt(i));
        }
        return tmp;
    }


    private char convertCharUTF8(char ch) {
        String tmp = "" + ch;
        if (a.contains(tmp)) return 'A';
        else if (e.contains(tmp)) return 'E';
        else if (i.contains(tmp)) return 'I';
        else if (o.contains(tmp)) return 'O';
        else if (u.contains(tmp)) return 'U';
        else if (y.contains(tmp)) return 'Y';
        else if (d.contains(tmp)) return 'D';
        else return Character.toUpperCase(ch);
    }
}
