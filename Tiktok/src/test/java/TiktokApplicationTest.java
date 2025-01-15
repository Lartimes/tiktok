import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lartimes.TiktokApplication;
import com.lartimes.tiktok.model.vo.FavoritesVo;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

/**
 * @author wüsch
 * @version 1.0
 * @description:
 * @since 2024/12/1 22:53
 */
@SpringBootTest(classes = TiktokApplication.class)
@ContextConfiguration
public class TiktokApplicationTest {
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void contextLoads() throws JsonProcessingException {
        FavoritesVo favoritesVo = new FavoritesVo();
        favoritesVo.setId(1);
        favoritesVo.setName("aaa");
        favoritesVo.setDescription("这是一个简洁");
        String s = objectMapper.writeValueAsString(favoritesVo);
        System.out.println(s);
    }

    @Test
    public void test() {

    }
}
