package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.user.User;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ItemRequestsRepositoryIntegrationTests {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Test
    public void findByRequestorId() {
        User user = User.builder().name("TestUser").email("test@mail.com").build();
        entityManager.persist(user);
        User user1 = User.builder().name("TestUser1").email("test1@mail.com").build();
        entityManager.persist(user1);
        ItemRequest itemRequest1 = new ItemRequest();
        itemRequest1.setRequestor(user);
        entityManager.persist(itemRequest1);

        ItemRequest itemRequest2 = new ItemRequest();
        itemRequest2.setRequestor(user);
        entityManager.persist(itemRequest2);
        ItemRequest itemRequest3 = new ItemRequest();
        itemRequest3.setRequestor(user1);
        entityManager.persist(itemRequest3);
        entityManager.flush();

        Pageable pageable = PageRequest.of(0, 10);
        Page<ItemRequest> resultPage = itemRequestRepository.findByRequestorId(user.getId(), pageable);

        assertThat(resultPage).isNotEmpty();
        assertThat(resultPage.getContent()).hasSize(2);
        assertThat(resultPage.getContent()).contains(itemRequest1, itemRequest2);
    }

    @Test
    public void findAllByRequestorIdNot() {
        User user = User.builder().name("TestUser").email("test@mail.com").build();
        entityManager.persist(user);
        User user1 = User.builder().name("TestUser1").email("test1@mail.com").build();
        entityManager.persist(user1);

        ItemRequest itemRequest1 = ItemRequest.builder().requestor(user).build();
        entityManager.persist(itemRequest1);
        ItemRequest itemRequest2 = ItemRequest.builder().requestor(user).build();
        itemRequest2.setRequestor(user);
        entityManager.persist(itemRequest2);
        ItemRequest itemRequest3 = ItemRequest.builder().requestor(user1).build();
        itemRequest3.setRequestor(user1);
        entityManager.persist(itemRequest3);
        entityManager.flush();

        Pageable pageable = PageRequest.of(0, 10);
        Page<ItemRequest> resultPage = itemRequestRepository.findAllByRequestorIdNot(user.getId(), pageable);

        assertThat(resultPage).isNotEmpty();
        assertThat(resultPage.getContent()).hasSize(1);
        assertThat(resultPage.getContent()).contains(itemRequest3);
    }
}
