package ee.omnifish.javaxjakarta;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import org.omnifaces.cdi.Eager;

@ApplicationScoped
@Eager
public class EagerBean {
    @PostConstruct
    public void init() {
        throw new RuntimeException("Fail");
    }
}
