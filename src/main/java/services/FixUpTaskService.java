
package services;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import repositories.FixUpTaskRepository;
import security.Authority;
import security.LoginService;
import domain.Actor;
import domain.Administrator;
import domain.Application;
import domain.Complaint;
import domain.Customer;
import domain.FixUpTask;

@Service
@Transactional
public class FixUpTaskService {

	// Managed repository

	@Autowired
	private FixUpTaskRepository		fixUpTaskRepository;

	// Suporting services

	@Autowired
	private ActorService			actorService;

	@Autowired
	private CustomerService			customerService;

	@Autowired
	private AdministratorService	administratorService;


	// Simple CRUD methods

	public FixUpTask create() {

		final Customer customer = this.customerService.findByPrincipal();
		Assert.notNull(customer);
		final Authority authority = new Authority();
		authority.setAuthority(Authority.CUSTOMER);
		Assert.isTrue(customer.getUserAccount().getAuthorities().contains(authority));

		final FixUpTask result = new FixUpTask();

		final Collection<Complaint> complaints = new HashSet<>();
		result.setComplaints(complaints);
		final Collection<Application> applications = new HashSet<>();
		result.setApplications(applications);

		return result;

	}
	public Collection<FixUpTask> findAll() {

		final Collection<FixUpTask> fixUpTasks = this.fixUpTaskRepository.findAll();

		Assert.notNull(fixUpTasks);

		return fixUpTasks;
	}

	public FixUpTask findOne(final int fixUpTaskID) {

		final FixUpTask fixUpTask = this.fixUpTaskRepository.findOne(fixUpTaskID);

		Assert.notNull(fixUpTask);

		return fixUpTask;

	}

	public FixUpTask save(final FixUpTask fixUpTask) {

		Assert.notNull(fixUpTask);

		final FixUpTask result;
		Customer customer = null;
		Administrator admin = null;

		final Authority authority1 = new Authority();
		authority1.setAuthority(Authority.CUSTOMER);
		final Authority authority2 = new Authority();
		authority2.setAuthority(Authority.ADMIN);

		if (LoginService.getPrincipal().getAuthorities().contains(authority1))
			customer = this.customerService.findByPrincipal();
		else if (LoginService.getPrincipal().getAuthorities().contains(authority2)) {
			admin = this.administratorService.findByPrincipal();
			customer = this.customerService.findByTask(fixUpTask);
		}

		Assert.isTrue(customer != null || admin != null);

		if (fixUpTask.getId() != 0) {

			Assert.isTrue(customer.getFixUpTasks().contains(fixUpTask));
			result = this.fixUpTaskRepository.save(fixUpTask);

		} else {
			final Date currentMoment = new Date(System.currentTimeMillis() - 1000);
			fixUpTask.setMoment(currentMoment);

			result = this.fixUpTaskRepository.save(fixUpTask);
			final Integer num = customer.getFixUpTasks().size();
			final Collection<FixUpTask> fixUpTasks = customer.getFixUpTasks();
			fixUpTasks.add(result);
			customer.setFixUpTasks(fixUpTasks);
			final Customer customer2 = this.customerService.save(customer);
			final Integer num2 = customer2.getFixUpTasks().size();
			Assert.isTrue(num + 1 == num2);
		}

		return result;

	}
	public void delete(final FixUpTask fixUpTask) {

		Assert.notNull(fixUpTask);
		Assert.isTrue(fixUpTask.getId() != 0);

		final Actor customer = this.actorService.findByPrincipal();
		Assert.notNull(customer);
		final Authority authority = new Authority();
		authority.setAuthority(Authority.CUSTOMER);
		Assert.isTrue(customer.getUserAccount().getAuthorities().contains(authority));
		final Customer c = (Customer) customer;
		Assert.isTrue(c.getFixUpTasks().contains(fixUpTask));
		Assert.isTrue(fixUpTask.getApplications().isEmpty());

		final Collection<FixUpTask> f = c.getFixUpTasks();
		f.remove(fixUpTask);
		c.setFixUpTasks(f);
		this.customerService.save(c);
		this.fixUpTaskRepository.delete(fixUpTask);
	}

	// Other business methods

	public Collection<Double> statsOfApplicationsPerFixUpTask() {

		final Collection<Double> result = this.fixUpTaskRepository.statsOfApplicationsPerFixUpTask();
		Assert.notNull(result);
		return result;

	}

	public Collection<Double> statsOfMaximumPricePerFixUpTask() {

		final Collection<Double> result = this.fixUpTaskRepository.statsOfMaximumPricePerFixUpTask();
		Assert.notNull(result);
		return result;
	}

	public Collection<Double> statsOfComplaintsPerFixUpTask() {

		final Collection<Double> result = this.fixUpTaskRepository.statsOfComplaintsPerFixUpTask();
		Assert.notNull(result);
		return result;

	}

	public Double ratioOfFixUpTasksWithComplaint() {

		final Double result = this.fixUpTaskRepository.ratioOfFixUpTasksWithComplaint();
		Assert.notNull(result);
		return result;

	}

	public Collection<FixUpTask> findFixUpTaskPerCategory(final int categoryId) {

		final Collection<FixUpTask> result;

		result = this.fixUpTaskRepository.findFixUpTaskPerCategory(categoryId);

		Assert.notNull(result);

		return result;
	}

	public FixUpTask findFixUpTaskPerComplaint(final int complaintId) {

		FixUpTask result;

		result = this.fixUpTaskRepository.findFixUpTaskPerComplaint(complaintId);

		Assert.notNull(result);

		return result;

	}

	public Integer countFixUpTaskByWarrantyId(final int warrantyId) {

		Integer result;

		result = this.fixUpTaskRepository.countFixUpTaskByWarrantyId(warrantyId);

		Assert.notNull(result);

		return result;
	}

}
