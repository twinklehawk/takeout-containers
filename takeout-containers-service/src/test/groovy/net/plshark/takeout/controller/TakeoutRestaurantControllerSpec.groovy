package net.plshark.takeout.controller

import java.time.OffsetDateTime
import net.plshark.takeout.exception.NotFoundException
import net.plshark.takeout.model.TakeoutRestaurant
import net.plshark.takeout.repository.TakeoutRestaurantRepository
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import spock.lang.Specification

class TakeoutRestaurantControllerSpec extends Specification {

    def repo = Mock(TakeoutRestaurantRepository)
    def controller = new TakeoutRestaurantController(repo)

    def 'create should set the create time and save the restaurant'() {
        def inserted = new TakeoutRestaurant(321L, 'test', 'plastic', OffsetDateTime.now())
        repo.insert({ r -> r.createTime != null && r.id == null && r.name == 'test' && r.containerType == 'plastic' }) >>
                Mono.just(inserted)

        expect:
        StepVerifier.create(controller.create(new TakeoutRestaurant(123L, 'test', 'plastic', null)))
                .expectNext(inserted)
                .verifyComplete()
    }

    def 'findById should return the matching object'() {
        def match = new TakeoutRestaurant(321L, 'test', 'plastic', OffsetDateTime.now())
        repo.findById(321) >> Mono.just(match)

        expect:
        StepVerifier.create(controller.findById(321))
                .expectNext(match)
                .verifyComplete()
    }

    def 'findById should throw a NotFoundException if no match is found'() {
        repo.findById(123) >> Mono.empty()

        expect:
        StepVerifier.create(controller.findById(123))
                .verifyError(NotFoundException)
    }

    def 'update should send the parsed request body to the repo'() {
        def request = new TakeoutRestaurant(1, 'arbys', 'plastic', OffsetDateTime.now())
        repo.update(request) >> Mono.just(1)

        expect:
        StepVerifier.create(controller.update(1, request))
                .expectNext(request)
                .verifyComplete()
    }

    def 'update should use the ID in the path and ignore the ID in the request body'() {
        def request = new TakeoutRestaurant(1, 'arbys', 'plastic', OffsetDateTime.now())
        def expected = new TakeoutRestaurant(5, 'arbys', 'plastic', request.createTime)
        repo.update(expected) >> Mono.just(1)

        expect:
        StepVerifier.create(controller.update(5, request))
                .expectNext(expected)
                .verifyComplete()
    }

    def 'update should return a NotFoundException if no record is updated'() {
        def request = new TakeoutRestaurant(1, 'arbys', 'plastic', OffsetDateTime.now())
        repo.update(_) >> Mono.just(0)

        expect:
        StepVerifier.create(controller.update(1, request))
                .verifyError(NotFoundException)
    }

    def 'delete should send the ID to the repo'() {
        repo.delete(8) >> Mono.just(1)

        expect:
        StepVerifier.create(controller.delete(8))
                .verifyComplete()
    }

    def 'delete should return a NotFoundException if no record is deleted'() {
        repo.delete(8) >> Mono.just(0)

        expect:
        StepVerifier.create(controller.delete(8))
                .verifyError(NotFoundException)
    }
}
