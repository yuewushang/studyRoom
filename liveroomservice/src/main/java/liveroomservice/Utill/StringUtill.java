package liveroomservice.Utill;

public class StringUtill {
    //私有化构造函数，使之成为工具类
    private StringUtill(){}

    /**
     * 判断字符串是否为空
     * @param s
     * @return
     */
    public static boolean isStringNull(String s){
        if(s!=null&&!s.equals("")){
            return true;
        }
        return false;
    }

}
