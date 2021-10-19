package br.com.alura.forum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

@SpringBootApplication
@EnableSpringDataWebSupport //  Com essa anotação habilitamos esse suporte, para o Spring pegar da requisição, dos parâmetros da url os campos, as informações de paginação e ordenação, e repassar isso para o Spring data
public class ForumApplication {

	public static void main(String[] args) {
		SpringApplication.run(ForumApplication.class, args);
	}

}
