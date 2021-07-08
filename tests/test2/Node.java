public class Node {
	public Node n;
	public void tester(int x)
	{
		int y = x + 10;
		if(y < 100)
		{
			Node n1 = new Node();
			n1.tester(y);
			n1 = new Node();
			n1.tester(y);
		}
	}
}
