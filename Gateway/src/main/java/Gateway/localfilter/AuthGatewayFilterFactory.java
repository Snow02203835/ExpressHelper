package Gateway.localfilter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Ming Qiu created in 2020/11/13 22:40
 **/
@Component
public class AuthGatewayFilterFactory extends AbstractGatewayFilterFactory<AuthFilter.Config> {

    public AuthGatewayFilterFactory() {
        super(AuthFilter.Config.class);
    }

    @Override
    public GatewayFilter apply(AuthFilter.Config config) {

        return new AuthFilter(config);
    }

    @Override
    public List<String> shortcutFieldOrder() {
        return new ArrayList<>(Collections.singleton("tokenName"));
    }

}
