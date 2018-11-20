
package services;

import java.util.Collection;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import repositories.ProfessionalRecordRepository;
import domain.ProfessionalRecord;

@Service
@Transactional
public class ProfessionalRecordService {

	// Managed Repository ------------------------
	@Autowired
	private ProfessionalRecordRepository	professionalRecordRepository;


	// Suporting services ------------------------

	//	@Autowired
	//	private CurriculumService			curriculumService;

	//	@Autowired
	//	private HandyWorkerService				handyWorkerService;

	// Simple CRUD methods -----------------------

	public ProfessionalRecord create() {

		ProfessionalRecord result;

		result = new ProfessionalRecord();

		return result;

	}

	public Collection<ProfessionalRecord> findAll() {

		Collection<ProfessionalRecord> result;

		result = this.professionalRecordRepository.findAll();
		Assert.notNull(result);

		return result;

	}

	public ProfessionalRecord findOne(final int professionalRecordId) {

		ProfessionalRecord result;

		result = this.professionalRecordRepository.findOne(professionalRecordId);

		return result;
	}

	public ProfessionalRecord save(final ProfessionalRecord professionalRecord) {

		Assert.notNull(professionalRecord);
		ProfessionalRecord result;

		result = this.professionalRecordRepository.save(professionalRecord);

		//		final HandyWorker handyWorker = handyWorkerService.findByPrincipal();
		//		Assert.notNull(handyWorker);
		//		Assert.notNull(handyWorker.getCurriculum());
		//
		//		final Curriculum curriculum = handyWorker.getCurriculum(); QUERY
		//		curriculum.getEndorserRecords().remove(endorserRecord);

		return result;
	}

	public void delete(final ProfessionalRecord professionalRecord) {

		Assert.notNull(professionalRecord);
		Assert.isTrue(professionalRecord.getId() != 0);

		//		final HandyWorker handyWorker = handyWorkerService.findByPrincipal();
		//		Assert.notNull(handyWorker);
		//		Assert.notNull(handyWorker.getCurriculum());
		//
		//		final Curriculum curriculum = handyWorker.getCurriculum();
		//		curriculum.getProfessionalRecords().remove(professionalRecord);

		this.professionalRecordRepository.delete(professionalRecord);

	}

	// Other business methods -----------------------
}
