public class Node {
	public Node n;
	public int f;
	public static void helper(Node obj)
	{
		int x = obj.f;
		x = x + 1;
	}
	public int test(int x)
	{
		this.f = 10;
		Node n1 = new Node();
		n = new Node();
		n.f = 20;
		n1.f = 25;
		int y = n.f + 20 + n1.f;
		n.test(10);
		n1.test(10);
		return y;
	}
}
