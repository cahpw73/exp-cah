package ch.swissbytes.fqmes.util.validator;

import ch.swissbytes.fqmes.control.user.UserService;

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
@FacesValidator("uniqueUsernameValidator")
public class UniqueUsernameValidator implements Validator {

    private static final Logger log = Logger.getLogger(UniqueUsernameValidator.class.getName());

    @Inject
    private UserService userService;


    @Override
    public void validate(FacesContext facesContext, UIComponent uiComponent, Object o) throws ValidatorException {
        Object inputValue = ((UIInput) facesContext.getViewRoot().findComponent("userCreateForm:inputUsername")).getSubmittedValue();
        String username = inputValue != null ? ((String)inputValue) : "";
        if (validate(username)) {
            //TODO The message of facesContext should use MessageProvider
            FacesMessage errMsg =    new FacesMessage("Username was already registered!");
            FacesContext.getCurrentInstance().addMessage(null, errMsg);
            errMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ValidatorException(errMsg);
        }
    }

    private boolean validate(String username) {
        return false;
        //return userService.existsUsername(username);
    }
}
