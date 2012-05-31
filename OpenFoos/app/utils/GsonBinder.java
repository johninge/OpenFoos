
package utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import play.data.binding.Global;
import play.data.binding.TypeBinder;

@Global
public class GsonBinder implements TypeBinder<JsonElement>
{

    @Override
    public Object bind(String name, Annotation[] annotations, String value, Class actualClass, Type genericType) throws Exception
    {
        return new JsonParser().parse(value);
    }
 
}