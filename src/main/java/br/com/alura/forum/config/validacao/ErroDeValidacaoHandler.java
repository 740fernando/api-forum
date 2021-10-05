package br.com.alura.forum.config.validacao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;


@RestControllerAdvice
public class ErroDeValidacaoHandler {

    @Autowired
    private MessageSource messageSource;


    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)

    public List<ErroDeFormularioDto> handler(MethodArgumentNotValidException exception) {
        List<ErroDeFormularioDto> dto = new ArrayList<>();

        List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();
        fieldErrors.forEach(e -> {
            String mensagem = messageSource.getMessage(e, LocaleContextHolder.getLocale());
            ErroDeFormularioDto erro = new ErroDeFormularioDto(e.getField(), mensagem);
            dto.add(erro);
        });
        return dto;
    }
}
/**
 * @RestControllerAdvice - Para interceptar as exceptions que forem lançadas nos métodos das classes controller,
 * devemos criar uma classe anotada com @RestControllerAdvice;
 *
 * messageSource; //Essa classe MessageSource te ajuda a pegar mensagens de erro, de acordo com o idioma que o cliente requisitar.
 *
 * String mensagem = messageSource.getMessage() e vou passar como parâmetro o erro e, e uma classe do
 * Spring chamada localeContextHolder.getLocaLe(), para ele descobrir qual o "locale", qual o local atual
 * para pegar a mensagem no idioma correto
 *
 * @ResponseStatus(code = HttpStatus.BAD_REQUEST) //define o status que a aplicacao vai devolver. O parâmetro será code = HttpStatus.BAD_REQUEST
O status code padrão a ser devolvido será o 200, mas é possível modificá-lo com a anotação @ResponseStatus.
 @ExceptionHandler(MethodArgumentNotValidException.class) // método deve ser chamado quando houver uma exceção dentro de algum Controller

 List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors(); //Essa variável tem os erros de formulário
 */