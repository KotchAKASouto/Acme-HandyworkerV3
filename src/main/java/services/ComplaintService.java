
package services;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import repositories.ComplaintRepository;
import security.Authority;
import domain.Actor;
import domain.Complaint;
import domain.Customer;
import domain.FixUpTask;
import domain.Referee;

@Service
@Transactional
public class ComplaintService {

	// Managed repository -------------------------------------------

	@Autowired
	private ComplaintRepository	complaintRepository;

	// Supporting services ------------------------------------------

	@Autowired
	private ActorService		actorService;

	@Autowired
	private CustomerService		customerservice;

	@Autowired
	private FixUpTaskService	fixUpTaskService;


	// Simple CRUD methods ------------------------------------------

	public Complaint create() {

		final Actor actor = this.actorService.findByPrincipal();
		Assert.notNull(actor);
		final Authority authority = new Authority();
		authority.setAuthority(Authority.CUSTOMER);
		Assert.isTrue((actor.getUserAccount().getAuthorities().contains(authority)));

		Complaint result;

		result = new Complaint();

		final Collection<String> attachments = new HashSet<>();

		result.setAttachments(attachments);

		return result;

	}

	public Collection<Complaint> findAll() {

		final Collection<Complaint> complaints;

		Assert.notNull(this.complaintRepository);

		complaints = this.complaintRepository.findAll();

		Assert.notNull(complaints);

		return complaints;

	}

	public Complaint findOne(final int complaintId) {

		Assert.isTrue(complaintId != 0);

		Assert.notNull(this.complaintRepository);

		final Complaint result = this.complaintRepository.findOne(complaintId);

		Assert.notNull(result);

		return result;

	}

	public Complaint save(final Complaint complaint) {

		Assert.notNull(complaint);
		Complaint result = null;

		final Actor actor = this.actorService.findByPrincipal();

		final Authority authority = new Authority();
		authority.setAuthority(Authority.REFEREE);
		Assert.isTrue(actor.getUserAccount().getAuthorities().contains(authority));

		final Referee referee = (Referee) actor;

		complaint.setReferee(referee);

		result = this.complaintRepository.save(complaint);

		return result;

	}

	public Complaint saveNewComplaint(final Complaint complaint, final int fixUpTaskId) {

		Assert.notNull(complaint);
		Complaint result = null;
		final FixUpTask fixUpTask = this.fixUpTaskService.findOne(fixUpTaskId);

		final Actor actor = this.actorService.findByPrincipal();

		final Authority authority = new Authority();
		authority.setAuthority(Authority.CUSTOMER);
		Assert.isTrue(actor.getUserAccount().getAuthorities().contains(authority));

		final Customer customer = (Customer) actor;

		final Date currentMoment = new Date(System.currentTimeMillis() - 1000);
		complaint.setMoment(currentMoment);

		result = this.complaintRepository.save(complaint);

		Collection<Complaint> complaints;
		complaints = customer.getComplaints();
		complaints.add(result);
		customer.setComplaints(complaints);

		Collection<Complaint> complaints2;
		complaints2 = fixUpTask.getComplaints();
		complaints2.add(result);
		fixUpTask.setComplaints(complaints2);

		this.customerservice.save(customer);

		this.fixUpTaskService.save(fixUpTask);

		return result;

	}

}
