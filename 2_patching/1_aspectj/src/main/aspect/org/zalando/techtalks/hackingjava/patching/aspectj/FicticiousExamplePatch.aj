package org.zalando.techtalks.hackingjava.patching.aspectj;

import org.zalando.techtalks.hackingjava.patching.baseline.FicticiousExample;

public aspect FicticiousExamplePatch{

      public pointcut integerMethodCalled(int value) : 
          execution(* FicticiousExample.yUNoReuseInteger(..)) && args(value);
      public pointcut stringMethodCalled(Iterable<String> iterable, String string) : 
          execution(* FicticiousExample.yUStringConcatInLoop(..)) && args(iterable, string);
      
      Integer around(int value) : integerMethodCalled(value){
          return Integer.valueOf(value);
      }
      
      String around(Iterable<String> iterable, String string) : stringMethodCalled(iterable, string){
          java.util.Iterator<String> iterator = iterable.iterator();
          StringBuilder sb = new StringBuilder();
          if(iterator.hasNext()){
              sb.append(iterator.next());
                  while(iterator.hasNext()){
                      sb.append(string).append(iterator.next());
                  }
          }
          return sb.toString();
      }

}