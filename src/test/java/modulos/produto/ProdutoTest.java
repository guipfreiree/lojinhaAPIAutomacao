package modulos.produto;

import dataFactory.ProdutoDataFactory;
import dataFactory.UsuarioDataFactory;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pojo.ComponentePojo;
import pojo.ProdutoPojo;
import pojo.UsuarioPojo;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.*;
import static io.restassured.matcher.RestAssuredMatchers.*;
import static org.hamcrest.Matchers.*;

@DisplayName("Testes de API Rest do módulo de Produto")
public class ProdutoTest {
    private String token;

    @BeforeEach
    public void beforeEach() {
        //configurar os dados da api rest
        baseURI = "http://165.227.93.41/";
        basePath = "/lojinha";

        //obter token do usuario admin
        this.token = given()
                .contentType(ContentType.JSON)
                .body(UsuarioDataFactory.realizarLogin("admin", "admin"))
            .when()
                .post("/v2/login")
            .then()
                .extract()
                    .path("data.token");
    }

    @Test
    @DisplayName("Validar que o valor do produto não pode ser 0.00")
    public void testValidarValorProdutoNaoPodeSerZero(){
        given()
            .contentType(ContentType.JSON)
            .header("token", this.token)
            .body(ProdutoDataFactory.criarProdutoComumComValorIgualA(0.00))
            .when()
                .post("/v2/produtos")
            .then()
                .assertThat()
                    .body("error", equalTo("O valor do produto deve estar entre R$ 0,01 e R$ 7.000,00"))
                    .statusCode(422);
    }

    @Test
    @DisplayName("Validar que o valor do produto não pode ser maior do que R$ 7000.00")
    public void testValidarValorProdutoNaoPodeSerMaiorDoQue7Mil(){
        //tentar inserir um produto com valor 7000.01
        given()
                .contentType(ContentType.JSON)
                .header("token", this.token)
                .body(ProdutoDataFactory.criarProdutoComumComValorIgualA(7000.01))
            .when()
                .post("/v2/produtos")
            .then()
                .assertThat()
                    .body("error", equalTo("O valor do produto deve estar entre R$ 0,01 e R$ 7.000,00"))
                    .statusCode(422);
    }
}
