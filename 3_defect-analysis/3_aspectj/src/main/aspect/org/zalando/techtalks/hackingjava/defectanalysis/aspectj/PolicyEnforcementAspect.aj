import java.util.*;
public aspect PolicyEnforcementAspect{

    pointcut hashTableOrVector() : call(* Hashtable.*(..)) || call(Hashtable.new(..)) 
                                || call(* Vector.*(..))    || call(Vector.new(..));
    declare error : hashTableOrVector() : "Hashtable and Vector are deprecated classes, don't use them!";
    
    pointcut inFrontendPackage()     : within(org..frontend..*);
    pointcut inServicePackage()      : within(org..service..*);
    pointcut inPersistencePackage()  : within(org..service..*);
    pointcut persistenceCall()       : call(* org..persistence..*.*(..));
    
    declare error : inFrontendPackage() && persistenceCall() :
    "The persistence package may not be accessed from the frontend package";

    declare warning : persistenceCall() && !(inServicePackage() || inPersistencePackage()) :
    "The persistence package should only be accessed by the service package (or by itself)";
}
