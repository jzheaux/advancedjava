public class MultipleInheritanceResolution {
  public static void main(String[] args) {
    ISpeak.test();
  }
}

interface ISpeak {
  default void speak() {
    System.out.println("ISpeak Speaking!");
  }

  public static void test() {
    new EmptySpeakImpl().speak();
    new EmptySpeakImplChild().speak();
  }
}

interface ISpeak2 extends ISpeak {
  default void speak() {
    System.out.println("ISpeak2 Speaking!");
  }
}

class EmptySpeakImpl implements ISpeak2 {}
class EmptySpeakImplChild extends EmptySpeakImpl implements ISpeak {}