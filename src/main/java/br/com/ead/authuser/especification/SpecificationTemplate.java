package br.com.ead.authuser.especification;

import br.com.ead.authuser.model.User;
import net.kaczmarzyk.spring.data.jpa.domain.Equal;
import net.kaczmarzyk.spring.data.jpa.domain.Like;
import net.kaczmarzyk.spring.data.jpa.web.annotation.And;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.data.jpa.domain.Specification;

public class SpecificationTemplate {
    @And({
    @Spec(path="userRole", spec= Equal.class),
    @Spec(path = "email", spec = Like.class)
    })
    public interface UserSpec extends Specification<User> {}
}
