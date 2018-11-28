
package services;

import java.util.Collection;
import java.util.HashSet;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import security.UserAccount;
import utilities.AbstractTest;
import domain.Complaint;
import domain.Customer;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
	"classpath:spring/datasource.xml", "classpath:spring/config/packages.xml"
})
@Transactional
public class ComplaintServiceTest extends AbstractTest {

	//Service under test ------------------------------------------

	@Autowired
	private ComplaintService	complaintService;

	@Autowired
	private CustomerService		customerservice;

	@Autowired
	private FixUpTaskService	fixUpTaskService;


	//Tests -------------------------------------------------------
	// TODO Complaint testing

	@Test
	public void testCreate() {

		this.authenticate("customer1");

		Complaint complaint;
		complaint = this.complaintService.create();

		Assert.isNull(complaint.getMoment());
		Assert.isTrue(complaint.getAttachments().isEmpty());
		Assert.isNull(complaint.getDescription());
		Assert.isNull(complaint.getReferee());
		Assert.isNull(complaint.getTicker());

	}

	@Test
	public void testSave() {

		super.authenticate("customer4");

		Complaint complaint;
		final Complaint saved;
		final Collection<Complaint> complaints;

		complaint = this.complaintService.create();
		complaint.setDescription("xxxx");
		complaint.setTicker("251118-ASRT");

		final Integer numberOfComplaints = this.customerservice.findByPrincipal().getComplaints().size();
		final Integer numberOfComplaints2 = this.fixUpTaskService.findOne(this.getEntityId("fixUpTask6")).getComplaints().size();

		saved = this.complaintService.saveNewComplaint(complaint, this.getEntityId("fixUpTask6"));

		final Integer numberOfComplaints3 = this.customerservice.findByPrincipal().getComplaints().size();
		final Integer numberOfComplaints4 = this.fixUpTaskService.findOne(this.getEntityId("fixUpTask6")).getComplaints().size();

		Assert.isTrue(numberOfComplaints + 1 == numberOfComplaints3);
		Assert.isTrue(numberOfComplaints2 + 1 == numberOfComplaints4);

		complaints = this.complaintService.findAll();
		Assert.isTrue(complaints.contains(saved));

	}

	@Test
	public void testFindOne() {

		this.createNewActorAndLogIn();

		Complaint complaint, saved, finded;

		complaint = this.complaintService.create();
		complaint.setDescription("xxxx");
		complaint.setTicker("251118-ASRT");

		final Integer numberOfComplaints = this.customerservice.findByPrincipal().getComplaints().size();

		saved = this.complaintService.save(complaint);

		final Integer numberOfComplaints2 = this.customerservice.findByPrincipal().getComplaints().size();

		Assert.isTrue(numberOfComplaints + 1 == numberOfComplaints2);

		finded = this.complaintService.findOne(saved.getId());
		Assert.isTrue(saved.equals(finded));

	}

	private void createNewActorAndLogIn() {

		Customer customer, saved1;
		Collection<Customer> customers;
		final Collection<Complaint> complaints = new HashSet<>();

		customer = this.customerservice.create();
		customer.setName("Gonz�lez");
		customer.setMiddleName("Adolfo");
		customer.setSurname("Gustavo");
		customer.setAddress("Calle Almoralejo");
		customer.setComplaints(complaints);

		final UserAccount userAccount = customer.getUserAccount();
		userAccount.setUsername("Gustavito");
		userAccount.setPassword("123123");

		customer.setUserAccount(userAccount);

		saved1 = this.customerservice.save(customer);
		customers = this.customerservice.findAll();
		Assert.isTrue(customers.contains(saved1));

		super.authenticate("Gustavito");

	}
}
