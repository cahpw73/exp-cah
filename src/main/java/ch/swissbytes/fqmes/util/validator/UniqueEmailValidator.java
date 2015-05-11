package ch.swissbytes.fqmes.util.validator;

import ch.swissbytes.Service.business.user.UserService;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.logging.Logger;

/**
 * Created by christian on 16/09/14.
 */
@Named
@FacesValidator("uniqueEmailValidator")
public class UniqueEmailValidator implements Validator {

    private static final Logger log = Logger.getLogger(UniqueEmailValidator.class.getName());

    @Inject
    private UserService userService;


    @Override
    public void validate(FacesContext facesContext, UIComponent uiComponent, Object toValidate) throws ValidatorException {
        log.info("Validator email exists");
        Object inputValue = ((UIInput) facesContext.getViewRoot().findComponent("userCreateForm:inputEmail")).getSubmittedValue();
        String email = inputValue != null ? ((String)inputValue) : "";
        if (validate(email)) {
            //TODO The message of facesContext should use MessageProvider
            FacesMessage errMsg =    new FacesMessage("Email was already registered!");
            FacesContext.getCurrentInstance().addMessage(null, errMsg);
            errMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(errMsg);
        }
    }

    private boolean validate(String email) {
        return false;
        //return userService.existsEmail(email);
    }
}
