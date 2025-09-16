document.addEventListener('DOMContentLoaded', async () => {
    try {
        const data = await ApiHelper.get('/api/profile');

        document.getElementById('profile-username').textContent = data.username || '';
        document.getElementById('profile-email').textContent = data.email || '';
        document.getElementById('profile-birthdate').textContent = data.birthDate || '';
        renderOrders(Array.isArray(data.orders) ? data.orders : []);

        // Проверяем роль и показываем кнопку админ панели если пользователь админ
        if (data.role === 'ADMIN') {
            const adminSection = document.getElementById('admin-panel-section');
            if (adminSection) {
                adminSection.style.display = 'block';
            }
        }

        Toast.success('Профиль загружен');
    } catch (e) {
        console.error('Failed to load profile:', e);
        // ApiHelper уже показал Toast с ошибкой
    }
});

document.getElementById('logout-btn')
    .addEventListener('click', async () => {
        await fetch('/logout', {method: 'POST', credentials: 'same-origin'});
        window.location.href = '/index.html';
    });

// Обработчик для кнопки админ панели
document.getElementById('admin-panel-btn')?.addEventListener('click', () => {
    window.location.href = '/frontend/admin.html';
});

const listElm = document.getElementById('orders-list');
const noOrdersEl = document.getElementById('no-orders');

// Модалка информации о товаре
const productInfoModal = document.getElementById('profile-product-info-modal');
const productInfoClose = document.getElementById('profile-product-info-close');
const productEditBtn = document.getElementById('profile-product-edit-btn');
const productDeleteBtn = document.getElementById('profile-product-delete-btn');

// Модалка редактирования товара в корзине
const cartEditModal = document.getElementById('profile-cart-edit-modal');
const cartEditCancelBtn = document.getElementById('profile-cart-edit-cancel');
const cartEditSaveBtn = document.getElementById('profile-cart-edit-save');
const cartEditQtyInput = document.getElementById('profile-cart-edit-qty');
const cartEditTotalEl = document.getElementById('profile-cart-edit-total');

let currentProduct = null;

function renderOrders(orders) {
    listElm.innerHTML = '';
    if (!orders.length) {
        noOrdersEl.style.display = 'flex';
        return;
    }
    noOrdersEl.style.display = 'none';

    orders.forEach(o => {
        const li = document.createElement('li');
        li.className = 'order-card';
        li.innerHTML = `
            <img class="order-card__img"
                 src="${o.imagePath || '/img/placeholder.png'}"
                 alt="${o.productName}"
                 data-product-id="${o.productId || o.id || ''}"
                 data-product-name="${o.productName || ''}"
                 data-product-price="${o.price || 0}"
                 data-product-quantity="${o.quantity || 0}"
                 data-product-image="${o.imagePath || '/img/placeholder.png'}">
            <div class="order-card__info">
                <p class="order-card__name">${o.productName}</p>
                <p class="order-card__price">${o.price.toLocaleString()} ₽ × ${o.quantity}</p>
            </div>`;

        // Добавляем обработчик клика на карточку товара
        li.addEventListener('click', () => openProductInfoModal(o));

        listElm.appendChild(li);
    });
    const addLi = document.createElement('li');
    addLi.className = 'add-card';
    addLi.innerHTML = '<div class="add-card__plus"></div>';
    addLi.addEventListener('click', openModal);
    listElm.appendChild(addLi);
}

const modal = document.getElementById('profile-cart-modal');
const cancelBtn = document.getElementById('profile-cancel-btn');
const confirmBtn = document.getElementById('profile-confirm-btn');
const addBtnEmpty = document.querySelector('.no-orders');
const selectEl = document.getElementById('profile-product-select');
const qtyInput = document.getElementById('profile-qty-input');
const totalPriceEl = document.getElementById('profile-total-price');

let productsCache = [];

addBtnEmpty.addEventListener('click', openModal);
cancelBtn.addEventListener('click', hideModal);
selectEl.addEventListener('change', updateTotal);
qtyInput.addEventListener('input', updateTotal);
confirmBtn.addEventListener('click', sendAddToCart);

async function openModal() {
    if (productsCache.length === 0) {
        try {
            const products = await ApiHelper.get('/api/cart');
            productsCache = products.filter(p => p.quantity > 0);
            rebuildSelectOptions();
        } catch (e) {
            console.error('Failed to load products:', e);
            // ApiHelper уже показал Toast с ошибкой
            return;
        }
    }

    if (!productsCache.length) {
        Toast.info('Все товары распроданы');
        return;
    }

    selectEl.selectedIndex = 0;
    qtyInput.value = '';
    updateTotal();

    modal.classList.remove('hidden');
    modal.setAttribute('aria-hidden', 'false');
    qtyInput.focus();
}

function hideModal() {
    modal.classList.add('hidden');
    modal.setAttribute('aria-hidden', 'true');
}

function rebuildSelectOptions() {
    selectEl.innerHTML = '';
    productsCache.forEach(p => {
        const text = `${p.name} — ${p.price} ₽ (остаток ${p.quantity})`;
        selectEl.add(new Option(text, p.id));
    });
}

function updateTotal() {
    qtyInput.value = qtyInput.value.replace(/\D/g, '');
    const id = Number(selectEl.value);
    const product = productsCache.find(p => p.id === id);
    if (!product || qtyInput.value === '') {
        totalPriceEl.textContent = 'Итого: —';
        confirmBtn.disabled = true;
        return;
    }
    let qty = Math.min(parseInt(qtyInput.value, 10), product.quantity);
    qty = Math.max(qty, 1);
    qtyInput.value = qty;
    totalPriceEl.textContent = `Итого: ${(product.price * qty).toLocaleString()} ₽`;
    confirmBtn.disabled = false;
}

async function sendAddToCart() {
    const id = Number(selectEl.value);
    const qty = Number(qtyInput.value);
    if (!qty) {
        return;
    }

    try {
        const cartData = await ApiHelper.post('/api/cart', {id, quantity: qty}, {
            showSuccessToast: true,
            successMessage: `Товар добавлен в корзину (${qty} шт.)`
        });

        renderOrders(cartData);

        // Обновляем кеш продуктов
        const prod = productsCache.find(p => p.id === id);
        if (prod) {
            prod.quantity -= qty;
            if (prod.quantity <= 0) {
                productsCache = productsCache.filter(p => p.id !== id);
            }
            rebuildSelectOptions();
        }

        hideModal();
    } catch (e) {
        console.error('Failed to add to cart:', e);
        // ApiHelper уже показал Toast с ошибкой (включая валидационные и бизнес-ошибки)
        // Не закрываем модал при ошибке, чтобы пользователь мог исправить данные
    }
}

const profileModal = document.getElementById('profile-edit-modal');
const profCancelBtn = document.getElementById('profile-edit-cancel');
const profSaveBtn = document.getElementById('profile-edit-save');
const profTitle = document.getElementById('profile-edit-modal-title');
const profLabel = document.getElementById('profile-edit-modal-label');

let currentField = null;      // '\u0438\u043c\u044f' | '\u043f\u043e\u0447\u0442\u0430' | '\u0434\u0430\u0442\u0430 \u0440\u043e\u0436\u0434\u0435\u043d\u0438\u044f'
let inputEl = null;

document.querySelectorAll('.btn-edit').forEach(btn => {
    btn.addEventListener('click', () => openEditModal(btn.dataset.field));
});
profCancelBtn.addEventListener('click', hideEditModal);
profSaveBtn.addEventListener('click', saveProfileField);

function openEditModal(field) {
    currentField = field;
    const currentValue = document
        .getElementById('profile-' + field)
        .textContent.trim();

    inputEl = document.createElement('input');
    inputEl.className = 'modal__input';
    inputEl.value = currentValue;
    inputEl.addEventListener('input', validateProfileInput);

    switch (field) {
        case 'username':
            profTitle.textContent = 'Изменить имя';
            inputEl.type = 'text';
            inputEl.maxLength = 50;
            break;
        case 'email':
            profTitle.textContent = 'Изменить e‑mail';
            inputEl.type = 'email';
            break;
        case 'birthdate':
            profTitle.textContent = 'Изменить дату рождения';
            inputEl.type = 'date';
            break;
    }
    profLabel.innerHTML = '';
    profLabel.appendChild(inputEl);
    validateProfileInput();
    profileModal.classList.remove('hidden');
    profileModal.setAttribute('aria-hidden', 'false');
    inputEl.focus();
}

function hideEditModal() {
    profileModal.classList.add('hidden');
    profileModal.setAttribute('aria-hidden', 'true');
    currentField = null;
    inputEl = null;
}

function validateProfileInput() {
    if (!inputEl) return;

    const v = inputEl.value.trim();
    let valid = false;

    if (currentField === 'username') {
        valid = v.length >= 2;
    } else if (currentField === 'email') {
        valid = /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(v);
    } else if (currentField === 'birthdate') {
        valid = v !== '';
    }

    profSaveBtn.disabled = !valid;
}

async function saveProfileField() {
    if (!currentField || !inputEl) return;

    const value = inputEl.value.trim();
    const payloadKey = currentField === 'birthdate' ? 'birthDate' : currentField;
    const payload = {[payloadKey]: value};

    let apiSuccess = false;

    try {
        await ApiHelper.patch('/api/profile', payload, {
            showSuccessToast: true,
            successMessage: `${currentField === 'username' ? 'Имя успешно обновлено' :
                currentField === 'email' ? 'Email успешно обновлён' :
                    'Дата рождения успешно обновлена'}`
        });
        apiSuccess = true;
    } catch (e) {
        console.error('Failed to save profile field:', e);
        return;
    }

    if (apiSuccess) {
        try {
            document.getElementById('profile-' + currentField).textContent = value;
        } catch (uiError) {
            console.error('Failed to update UI:', uiError);
            Toast.info('Данные сохранены. Обновите страницу для отображения изменений.');
        }
        hideEditModal();
    }
}

// Функции для модалки информации о товаре
function openProductInfoModal(product) {
    currentProduct = product;

    // Заполняем модалку информацией о товаре
    document.getElementById('profile-product-info-image').src = product.imagePath || '/img/placeholder.png';
    document.getElementById('profile-product-info-name').textContent = product.productName || '';
    document.getElementById('profile-product-info-price').textContent = `${(product.price || 0).toLocaleString()} ₽`;
    document.getElementById('profile-product-info-quantity').textContent = product.quantity || 0;
    const total = (product.price || 0) * (product.quantity || 1);
    document.getElementById('profile-product-info-total').textContent = `${total.toLocaleString()} ₽`;

    // Показываем модалку
    productInfoModal.classList.remove('hidden');
    productInfoModal.setAttribute('aria-hidden', 'false');
}

function closeProductInfoModal() {
    productInfoModal.classList.add('hidden');
    productInfoModal.setAttribute('aria-hidden', 'true');
    currentProduct = null;
}

// Обработчики для модалки информации о товаре
productInfoClose.addEventListener('click', closeProductInfoModal);

productEditBtn.addEventListener('click', () => {
    // Сохраняем данные товара перед закрытием модалки
    const productToEdit = currentProduct;
    closeProductInfoModal();

    // Восстанавливаем данные товара для модалки редактирования
    currentProduct = productToEdit;
    openCartEditModal();
});

productDeleteBtn.addEventListener('click', async () => {
    if (!currentProduct) return;

    try {
        const cartData = await ApiHelper.delete(`/api/cart/${currentProduct.productId || currentProduct.id}`, {
            showSuccessToast: true,
            successMessage: 'Товар удалён из корзины'
        });

        renderOrders(cartData);
        closeProductInfoModal();

        // Сбрасываем кеш продуктов при удалении товара
        productsCache = [];
    } catch (e) {
        console.error('Failed to delete product from cart:', e);
    }
});

// Функции для модалки редактирования товара в корзине
function openCartEditModal() {
    if (!currentProduct) return;

    // Заполняем модалку информацией о товаре
    document.getElementById('profile-cart-edit-image').src = currentProduct.imagePath || '/img/placeholder.png';
    document.getElementById('profile-cart-edit-name').textContent = currentProduct.productName || '';
    document.getElementById('profile-cart-edit-price').textContent = `${(currentProduct.price || 0).toLocaleString()} ₽`;
    cartEditQtyInput.value = currentProduct.quantity || 1;
    updateCartEditTotal();

    // Показываем модалку
    cartEditModal.classList.remove('hidden');
    cartEditModal.setAttribute('aria-hidden', 'false');
    cartEditQtyInput.focus();
}

function closeCartEditModal() {
    cartEditModal.classList.add('hidden');
    cartEditModal.setAttribute('aria-hidden', 'true');
    currentProduct = null;
}

function updateCartEditTotal() {
    const qty = parseInt(cartEditQtyInput.value, 10) || 0;
    const price = currentProduct?.price || 0;
    const total = price * qty;
    cartEditTotalEl.textContent = `Итого: ${total.toLocaleString()} ₽`;
    cartEditSaveBtn.disabled = qty <= 0;
}

// Обработчики для модалки редактирования товара
cartEditCancelBtn.addEventListener('click', closeCartEditModal);

cartEditQtyInput.addEventListener('input', () => {
    cartEditQtyInput.value = cartEditQtyInput.value.replace(/\D/g, '');
    updateCartEditTotal();
});

cartEditSaveBtn.addEventListener('click', async () => {
    if (!currentProduct) return;

    const newQty = parseInt(cartEditQtyInput.value, 10);
    if (newQty <= 0) return;

    try {
        const cartData = await ApiHelper.patch('/api/cart', {
            id: currentProduct.productId || currentProduct.id,
            quantity: newQty
        }, {
            showSuccessToast: true,
            successMessage: `Количество изменено на ${newQty} шт.`
        });

        renderOrders(cartData);
        closeCartEditModal();

        // Сбрасываем кеш продуктов при изменении количества
        productsCache = [];
    } catch (e) {
        console.error('Failed to update cart item:', e);
    }
});

// Закрытие модалок по клику на фон
productInfoModal.addEventListener('click', (e) => {
    if (e.target === productInfoModal) {
        closeProductInfoModal();
    }
});

cartEditModal.addEventListener('click', (e) => {
    if (e.target === cartEditModal) {
        closeCartEditModal();
    }
});