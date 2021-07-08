class T18 {

    public static void main(String[] args)
    {
         System.out.println(new A().isSpecial(17));

    }
}


class A{

  public boolean retTrue(){
    return true;
  }
  public boolean retFalse(){
    return false;
  }
  public boolean isSpecial(int n)
  {
      int i;
      boolean ans;
      ans = true;
      i = 2;
      if (n <= 1)
      ans = this.retTrue();
      while((i <= n) && (2 <= n))
      {
          if ((n / i) != 0)
            ans = this.retFalse();
          i = i + 1;
      }
      return ans;
  }
}
