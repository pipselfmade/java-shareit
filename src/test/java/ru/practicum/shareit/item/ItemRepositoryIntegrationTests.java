package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ItemRepositoryIntegrationTests {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ItemRepository itemRepository;

    @Test
    public void findAllByOwnerId() {
        User user = User.builder().name("TestUser").email("test@mail.com").build();
        entityManager.persist(user);
        User user1 = User.builder().name("TestUser1").email("test1@mail.com").build();
        entityManager.persist(user1);
        Item item1 = Item.builder().owner(user).build();
        entityManager.persist(item1);
        Item item2 = Item.builder().owner(user).build();
        entityManager.persist(item2);
        Item item3 = Item.builder().owner(user1).build();
        entityManager.persist(item3);
        entityManager.flush();
        List<Item> items = itemRepository.findAllByOwnerId(user.getId()).stream().collect(Collectors.toList());

        assertThat(items).hasSize(2);
        assertThat(items).contains(item1, item2);
        assertThat(items.get(0).getId()).isEqualTo(item1.getId());
        assertThat(items.get(0).getName()).isEqualTo(item1.getName());
        assertThat(items.get(0).getDescription()).isEqualTo(item1.getDescription());
        assertThat(items.get(1).getId()).isEqualTo(item2.getId());
        assertThat(items.get(1).getName()).isEqualTo(item2.getName());
        assertThat(items.get(1).getDescription()).isEqualTo(item2.getDescription());
    }

    @Test
    public void findByNameOrDescriptionAvailable() {
        String text = "Дрель";
        Pageable pageable = PageRequest.of(0, 10);
        Item item1 = Item.builder().name("Дрель").description("Эллектрическая дрель").available(true).build();
        entityManager.persist(item1);
        Item item2 = Item.builder().name("Ручная Дрель").description("Ручная дрель").available(true).build();
        entityManager.persist(item2);
        Item item3 = Item.builder().name("Ручная Дрель").description("Ручная дрель").available(false).build();
        entityManager.persist(item3);
        entityManager.flush();
        Page<Item> resultPage = itemRepository.findByNameOrDescriptionAvailable(text, pageable);
        List<Item> items = resultPage.getContent();

        assertThat(items).hasSize(2);
        assertThat(items).contains(item1, item2);
        assertThat(items.get(0).getId()).isEqualTo(item1.getId());
        assertThat(items.get(0).getName()).isEqualTo(item1.getName());
        assertThat(items.get(0).getDescription()).isEqualTo(item1.getDescription());
        assertThat(items.get(1).getId()).isEqualTo(item2.getId());
        assertThat(items.get(1).getName()).isEqualTo(item2.getName());
        assertThat(items.get(1).getDescription()).isEqualTo(item2.getDescription());
    }

    @Test
    public void findAllByRequestId() {
        ItemRequest itemRequest = ItemRequest.builder().build();
        entityManager.persist(itemRequest);
        ItemRequest itemRequest1 = ItemRequest.builder().build();
        entityManager.persist(itemRequest1);
        Item item1 = Item.builder().request(itemRequest).build();
        entityManager.persist(item1);
        Item item2 = Item.builder().request(itemRequest).build();
        entityManager.persist(item2);
        Item item3 = Item.builder().request(itemRequest1).build();
        entityManager.persist(item3);
        entityManager.flush();
        List<Item> items = itemRepository.findAllByRequestId(itemRequest.getId());

        assertThat(items).hasSize(2);
        assertThat(items).contains(item1, item2);
        assertThat(items.get(0).getId()).isEqualTo(item1.getId());
        assertThat(items.get(0).getName()).isEqualTo(item1.getName());
        assertThat(items.get(0).getDescription()).isEqualTo(item1.getDescription());
        assertThat(items.get(1).getId()).isEqualTo(item2.getId());
        assertThat(items.get(1).getName()).isEqualTo(item2.getName());
        assertThat(items.get(1).getDescription()).isEqualTo(item2.getDescription());
    }
}
