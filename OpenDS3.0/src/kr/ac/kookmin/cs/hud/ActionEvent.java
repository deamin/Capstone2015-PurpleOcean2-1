package kr.ac.kookmin.cs.hud;

public class ActionEvent {
  protected String actionName;
  protected String controllerName;
  
  public ActionEvent(String actionName, String controllerName) {
    this.actionName = actionName;
    this.controllerName = controllerName;
  }
  
  public String getActionName()
  {
    return actionName;
  }
  
  public String getControllerName()
  {
    return controllerName;
  }
}