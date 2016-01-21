import java.util.*;
public aspect PolicyEnforcementAspect{

    pointcut hashTableOrVector() : call(* Hashtable.*(..)) || call(Hashtable.new(..)) 
                                || call(* Vector.*(..))    || call(Vector.new(..));
    declare error : hashTableOrVector() : "Hashtable and Vector are deprecated classes, don't use them!";
}