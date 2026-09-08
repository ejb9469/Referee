package testing;

import adjudication.Judge;
import adjudication.SzykmanReferee;
import domain.Order;
import util.Orders;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TestCaseReferee extends TestCase {


    public TestCaseReferee(String name, Order... orders) {
        super(name, orders);
    }

    public TestCaseReferee(String name, List<Order> orders) {
        super(name, orders);
    }

    public TestCaseReferee(TestCase testCase) {
        super(testCase);
    }

    @Override
    protected void judge() {

        Judge judge;
        if (!orders.isEmpty())
            judge = new SzykmanReferee(new ArrayList<>(orders));
        else
            judge = new SzykmanReferee();

        judge.judge();
        Collection<Order> adjudicatedOrders =
                Orders.conformOrder(judge.getOrders(), this.orders);

        for (Order order : adjudicatedOrders)
            actualFields.add(new boolean[]{order.verdict});  // Could expand with more fields later

    }


}