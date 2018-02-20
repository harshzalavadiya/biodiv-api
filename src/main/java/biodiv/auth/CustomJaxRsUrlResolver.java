package biodiv.auth;

import org.pac4j.core.context.WebContext;
import org.pac4j.core.http.UrlResolver;
import org.pac4j.jax.rs.pac4j.JaxRsUrlResolver;
import org.pac4j.jax.rs.pac4j.JaxRsContext;

public class CustomJaxRsUrlResolver extends JaxRsUrlResolver {

    @Override
    public String compute(String url, WebContext context) {
        System.out.println(url);
        if (context instanceof JaxRsContext && url != null) {
            String x = ((JaxRsContext) context).getAbsolutePath(url, true);
            System.out.println(x);
            return x;
        }
        return null;
    }

}
