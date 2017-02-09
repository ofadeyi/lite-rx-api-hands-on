package io.pivotal.literx;

import io.pivotal.literx.domain.User;
import io.pivotal.literx.repository.ReactiveRepository;
import io.pivotal.literx.repository.ReactiveUserRepository;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

/**
 * Learn how to control the demand.
 *
 * @author Sebastien Deleuze
 */
public class Part06Request {

	ReactiveRepository<User> repository = new ReactiveUserRepository();

//========================================================================================

	@Test
	public void requestAll() {
		Flux<User> flux = repository.findAll();
		StepVerifier verifier = requestAllExpectFour(flux);
		verifier.verify();
	}

	// TODO Create a StepVerifier that requests initially all values and expect a 4 values to be received
	StepVerifier requestAllExpectFour(Flux<User> flux) {
		return StepVerifier.create(flux)
				.expectNextCount(4)
				.expectComplete();
	}

//========================================================================================

	@Test
	public void requestOneByOne() {
		Flux<User> flux = repository.findAll();
		StepVerifier verifier = requestOneExpectSkylerThenRequestOneExpectJesse(flux);
		verifier.verify();
	}

	// TODO Create a StepVerifier that requests initially 1 value and expects {@link User.SKYLER} then requests another value and expects {@link User.JESSE}.
	StepVerifier requestOneExpectSkylerThenRequestOneExpectJesse(Flux<User> flux) {
		return StepVerifier.create(flux)
				.expectNext(User.SKYLER)
				.expectNext(User.JESSE)
				.thenCancel();
	}

//========================================================================================

	@Test
	public void experimentWithLog() {
		Flux<User> flux = fluxWithLog();
		StepVerifier.create(flux, 0)
				.thenRequest(1)
				.expectNextMatches(u -> true)
				.thenRequest(1)
				.expectNextMatches(u -> true)
				.thenRequest(2)
				.expectNextMatches(u -> true)
				.expectNextMatches(u -> true)
				.verifyComplete();
	}

	// TODO Return a Flux with all users stored in the repository that prints automatically logs for all Reactive Streams signals
	Flux<User> fluxWithLog() {
        Flux<User> flux = repository.findAll();
		return flux.log();
	}


//========================================================================================

	@Test
	public void experimentWithDoOn() {
		Flux<User> flux = fluxWithDoOnPrintln();
		StepVerifier.create(flux)
				.expectNextCount(4)
				.verifyComplete();
	}

	// TODO Return a Flux with all users stored in the repository that prints
    // "Starring:" on subscribe,
    // "firstname lastname" for all values and
    // "The end!" on complete
	Flux<User> fluxWithDoOnPrintln() {
        Flux<User> flux = repository.findAll();
		return flux
                .doOnSubscribe(user -> System.out.println("Starring:"))
                .doOnNext(user -> System.out.println(String.format("%s %s", user.getFirstname(), user.getLastname())))
                .doOnComplete(() -> System.out.println("The end!"));
	}

}
