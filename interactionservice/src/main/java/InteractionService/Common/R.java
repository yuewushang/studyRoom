package InteractionService.Common;

import lombok.Data;

//返回结果模板类
@Data
public class R<T> {
    private Integer code;//结果码
    private String msg;//错误消息
    private T data;//返回的数据

    //这是一个泛型的方法，成功
    public static <T> R<T> success(T object){
        //创建一个结果对象
        R<T> r=new R<>();
        r.code=1;//1为成功
        r.data=object;
        return r;
    }

    //这是一个泛型方法，失败
    public static <T> R<T> error(String msg){
        R<T> r=new R<>();
        r.code=-1;
        r.msg=msg;
        return r;
    }
}
