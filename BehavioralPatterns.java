import java.util.ArrayList;
import java.util.List;


// State Pattern
   class ATMContext {
      private ATMState state;
      private ATM_UISubject atmUI;

      public ATMContext(){
         state = null;
         atmUI = new ATM_UISubject();

         new ScreenObserver(atmUI);
         (new MoneySlotObserver(atmUI)).setCountingStrategy(new ChainMoneyCounting());
         // (new MoneySlotObserver(atmUI)).setCountingStrategy(new SimpleMoneyCounting());
      } 

      public void setState(ATMState state){
         this.state = state;

         atmUI.setState(state);  
      }

      public ATMState getState(){
         return state;
      }
   }

   interface ATMState {
      public void doAction(ATMContext context);
   }

   class GetPINState implements ATMState {

      public void doAction(ATMContext context) {
         context.setState(this); 
      }

      public String toString(){
         return "Please insert your PIN number.";
      }
   }

   class GetMoneyNumberState implements ATMState {

      public void doAction(ATMContext context) {
         context.setState(this); 
      }

      public String toString(){
         return "Insert the number of money your want to get out.";
      }
   }

   class ReturnMoneyState implements ATMState {
      int totalMoney;

      public void doAction(ATMContext context) {
         context.setState(this);
      }

      public void setTotalMoney (int money) {
         totalMoney = money;
      }

      public int getTotalMoney () {
         return totalMoney;
      }

      public String toString(){
         return "Take your money.";
      }
   }

   class ErrorState implements ATMState {

      public void doAction(ATMContext context) {
         context.setState(this); 
      }

      public String toString(){
         return "There was an error with the PIN.";
      }
   }

// Observer
   class ATM_UISubject {
      
      private List<ATM_UIObserver> observers = new ArrayList<ATM_UIObserver>();
      private ATMState atmState;

      public ATMState getState() {
         return atmState;
      }

      public void setState(ATMState state) {
         this.atmState = state;
         notifyAllObservers();
      }

      public void attach(ATM_UIObserver observer){
         observers.add(observer);      
      }

      public void notifyAllObservers(){
         for (ATM_UIObserver observer : observers) {
            observer.update();
         }
      }  
   }


   abstract class ATM_UIObserver {
      protected ATM_UISubject subject;
      public abstract void update();
   }

   class MoneySlotObserver extends ATM_UIObserver{
      MoneyCountingStrategy countingStrategy;

      public void setCountingStrategy (MoneyCountingStrategy strategy) {
         countingStrategy = strategy;
      }

      public MoneySlotObserver(ATM_UISubject subject){
         this.subject = subject;
         this.subject.attach(this);
      }

      @Override
      public void update() {
         ATMState currentState = subject.getState();
         if (currentState instanceof ReturnMoneyState) {

            AbstractMoneyObject moneyObject = countingStrategy.splitMoneyOperation(((ReturnMoneyState)currentState).getTotalMoney());

            if (moneyObject.isNil()) {
               System.out.println("ATM slot error!!!");
            }
            else {
               System.out.print("ATM money slot: ");
               for (Integer bill : moneyObject.returnMoneyList) {
                  System.out.printf("%d, ", bill);
               }
               System.out.println("");
            }

            
         }
          
      }
   }

   class ScreenObserver extends ATM_UIObserver{
      public ScreenObserver(ATM_UISubject subject){
         this.subject = subject;
         this.subject.attach(this);
      }

      @Override
      public void update() {
         System.out.println( subject.getState().toString() );
      }
   }

// Null Object
   abstract class AbstractMoneyObject {
      public List<Integer> returnMoneyList = new ArrayList<Integer>();

      public abstract boolean isNil();
   }

   class RealMoneyObject extends AbstractMoneyObject {
      public boolean isNil () {
         return false;
      }
   }

   class NullMoneyObject extends AbstractMoneyObject {
      public boolean isNil () {
         return true;
      }
   }

// Chain of Responsibilities
   abstract class AbstractMoneyChain {
      protected AbstractMoneyChain nextMoneyChain;

      public void setNextMoneyChain(AbstractMoneyChain nextMoneyChain){
         this.nextMoneyChain = nextMoneyChain;
      }

      public abstract void findMoney(AbstractMoneyObject moneyObject, int money);  
   }

   // The chain
      class MoneyChain100 extends AbstractMoneyChain {
         public void findMoney(AbstractMoneyObject moneyObject, int money) {
            while (money - 100 >= 0) {
               money -= 100;
               moneyObject.returnMoneyList.add(100);
            }

            if (money > 0) {
               nextMoneyChain.findMoney(moneyObject, money);
            }
         }
      }

      class MoneyChain50 extends AbstractMoneyChain {
         public void findMoney(AbstractMoneyObject moneyObject, int money) {
            while (money - 50 >= 0) {
               money -= 50;
               moneyObject.returnMoneyList.add(50);
            }

            if (money > 0) {
               nextMoneyChain.findMoney(moneyObject, money);
            }
         }
      }

      class MoneyChain20 extends AbstractMoneyChain {
         public void findMoney(AbstractMoneyObject moneyObject, int money) {
            while (money - 20 >= 0) {
               money -= 20;
               moneyObject.returnMoneyList.add(20);
            }

            if (money > 0) {
               nextMoneyChain.findMoney(moneyObject, money);
            }
         }
      }

      class MoneyChain5 extends AbstractMoneyChain {
         public void findMoney(AbstractMoneyObject moneyObject, int money) {
            while (money - 5 >= 0) {
               money -= 5;
               moneyObject.returnMoneyList.add(5);
            }

            if (money > 0) {
               nextMoneyChain.findMoney(moneyObject, money);
            }
         }
      }

      class MoneyChain1 extends AbstractMoneyChain {
         public void findMoney(AbstractMoneyObject moneyObject, int money) {
            while (money - 1 >= 0) {
               money -= 1;
               moneyObject.returnMoneyList.add(1);
            }

            if (money > 0) {
               nextMoneyChain.findMoney(moneyObject, money);
            }
         }
      }


// Strategy
   interface MoneyCountingStrategy {
      AbstractMoneyObject splitMoneyOperation (int totalMoney); 
   }

   class SimpleMoneyCounting implements MoneyCountingStrategy {
      public AbstractMoneyObject splitMoneyOperation (int totalMoney) {
         if (totalMoney < 1) {
            return new NullMoneyObject();
         }
         else {
            RealMoneyObject returnMoney = new RealMoneyObject();

            int [] availableBills = {100, 50, 20, 10, 5, 1};
            while (totalMoney > 0) {
               for (int i=0; i<6; i++) {
                  if (totalMoney - availableBills[i] >= 0) {
                     totalMoney -= availableBills[i];
                     returnMoney.returnMoneyList.add(availableBills[i]);
                  }
               }
            }

            return returnMoney;
         }
         
      }  
   }

   class ChainMoneyCounting implements MoneyCountingStrategy {
      public AbstractMoneyObject splitMoneyOperation (int totalMoney) {
         if (totalMoney > 0) {
            RealMoneyObject returnMoney = new RealMoneyObject();

            AbstractMoneyChain chain100 = new MoneyChain100();
            AbstractMoneyChain chain50 = new MoneyChain50();
            AbstractMoneyChain chain20 = new MoneyChain20();
            AbstractMoneyChain chain5 = new MoneyChain5();
            AbstractMoneyChain chain1 = new MoneyChain1();

            chain100.setNextMoneyChain(chain50);
            chain50.setNextMoneyChain(chain20);
            chain20.setNextMoneyChain(chain5);
            chain5.setNextMoneyChain(chain1);

            chain100.findMoney(returnMoney, totalMoney);

            return returnMoney;
         }
         else {
            return new NullMoneyObject();   
         }
         
      }
   }


public class BehavioralPatterns {
   public static void main(String[] args) {
      ATMContext context = new ATMContext();
      GetPINState startState = new GetPINState();
      startState.doAction(context);

      String pin = System.console().readLine();

      // System.out.print(pin);

      if (pin.equals("1234")) {
         (new GetMoneyNumberState()).doAction(context);

         int moneyNumber = Integer.parseInt(System.console().readLine());

         ReturnMoneyState returnMoneyState = new ReturnMoneyState();
         returnMoneyState.setTotalMoney(moneyNumber);

         returnMoneyState.doAction(context);
      }
      else {
         (new ErrorState()).doAction(context);
      }
   }
}