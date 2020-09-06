import com.reimu.boot.Application;
import com.reimu.shiro.HashEncryptService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = Application.class,webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class HashEncryptServiceTest {

    @Test
    public void sha512Test() {
        assertEquals(HashEncryptService.sha512Encrypt("123456".toCharArray(),"salt"),"fa2f4e74a62734244f2d0c8f81ef56514b0be577adec0ddddf07d87ecbadef219f96fbae36b916186f880b5f9899d3af87bf086821666ea3d802f0db73455516");
    }

}
