document.addEventListener('DOMContentLoaded', async () => {
    try {
        const users = await ApiHelper.get('/api/admin/users');
        const products = await ApiHelper.get('/api/admin/products');

        renderUsers(Array.isArray(users) ? users : []);

        renderProducts(Array.isArray(products) ? products : []);

        Toast.success('Админ панель загружена');
    } catch (e) {
        console.error('Failed to load admin panel:', e);

    }
});

// Функциональность выхода
document.getElementById('logout-btn')
    .addEventListener('click', async () => {
        await fetch('/logout', {method: 'POST', credentials: 'same-origin'});
        window.location.href = '/index.html';
    });


const listElm = document.getElementById('users-list');
const noOrdersEl = document.getElementById('no-users');
const productsListElm = document.getElementById('products-list');
const noProductsEl = document.getElementById('no-products');

// Управление пользователями
function renderUsers(users) {
    listElm.innerHTML = '';
    if (!users.length) {
        noOrdersEl.style.display = 'flex';
        return;
    }
    noOrdersEl.style.display = 'none';

    users.forEach(user => {
        const li = document.createElement('li');
        li.className = 'user-card';
        li.innerHTML = `
            <img class="user-card__img"
                 src="/frontend/img/profile/main/profile/profile avatar.png"
                 alt="${user.username || 'User'}"
                 data-user-id="${user.id || ''}"
                 data-user-name="${user.username || ''}"
                 data-user-email="${user.email || ''}"
                 data-user-role="${user.role || 'USER'}"
                 data-user-created="${user.createdAt || ''}">
            <div class="user-card__info">
                <p class="user-card__name">${user.username || 'Без имени'}</p>
                <p class="user-card__email">${user.email || 'Без email'}</p>
                <p class="user-card__role">Роль: ${user.role || 'USER'}</p>
            </div>`;
        listElm.appendChild(li);
    });
}

function renderProducts(products) {
    productsListElm.innerHTML = '';
    if (!products.length) {
        noProductsEl.style.display = 'flex';
        return;
    }
    noProductsEl.style.display = 'none';

    products.forEach(product => {
        const li = document.createElement('li');
        li.className = 'order-card';

        const stockClass = product.quantity === 0 ? 'out-of-stock' : 'in-stock';
        const stockText = product.quantity === 0 ? 'Нет в наличии' : `В наличии: ${product.quantity}`;

        li.innerHTML = `
            <img class="order-card__img"
                 src="${product.imagePath || '/frontend/img/profile/main/profile/profile avatar.png'}"
                 alt="${product.name || 'Product'}"
                 data-product-id="${product.id || ''}"
                 data-product-name="${product.name || ''}"
                 data-product-price="${product.price || 0}"
                 data-product-quantity="${product.quantity || 0}"
                 data-product-image="${product.imagePath || ''}">
            <div class="order-card__info">
                <p class="order-card__name">${product.name || 'Без названия'}</p>
                <p class="order-card__price">${product.price || 0} ₽</p>
                <p class="order-card__stock ${stockClass}">${stockText}</p>
            </div>`;
        productsListElm.appendChild(li);
    });

    // Кнопка добавления в конец списка
    const addLi = document.createElement('li');
    addLi.className = 'add-card';
    addLi.innerHTML = '<div class="add-card__plus"></div>';
    addLi.addEventListener('click', openAddProductModal);
    productsListElm.appendChild(addLi);
}

listElm.addEventListener('click', (e) => {
    const userCard = e.target.closest('.user-card');
    if (userCard) {
        const img = userCard.querySelector('.user-card__img');
        if (img) {
            const userData = {
                userId: img.dataset.userId,
                userName: img.dataset.userName,
                userEmail: img.dataset.userEmail,
                userRole: img.dataset.userRole,
                userCreated: img.dataset.userCreated
            };
            openUserModal(userData);
        }
    }
});

productsListElm.addEventListener('click', (e) => {
    const productCard = e.target.closest('.order-card');
    if (productCard) {
        const img = productCard.querySelector('.order-card__img');
        if (img) {
            const productData = {
                productId: img.dataset.productId,
                productName: img.dataset.productName,
                productPrice: img.dataset.productPrice,
                productQuantity: img.dataset.productQuantity,
                productImage: img.dataset.productImage
            };
            openProductModal(productData);
        }
    }
});

const modal = document.getElementById('modal');
const cancelBtn = document.getElementById('cancel-btn');
const confirmBtn = document.getElementById('confirm-btn');
const addBtnEmpty = document.getElementById('no-users');
const selectEl = document.getElementById('product-select');
const qtyInput = document.getElementById('qty-input');
const totalPriceEl = document.getElementById('total-price');

let productsCache = [];

addBtnEmpty.addEventListener('click', openModal);
cancelBtn.addEventListener('click', hideModal);
selectEl.addEventListener('change', updateTotal);
qtyInput.addEventListener('input', updateTotal);
confirmBtn.addEventListener('click', sendAddToCart);

async function openModal() {
    if (productsCache.length === 0) {
        try {
            const products = await ApiHelper.get('/api/admin/products');
            productsCache = products.filter(p => p.quantity >= 0);
            rebuildSelectOptions();
        } catch (e) {
            console.error('Failed to load products:', e);
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

        renderUsers(cartData);

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
    }
}

// Элементы модального окна профиля
const profileModal = document.getElementById('profile-modal');
const profCancelBtn = document.getElementById('profile-cancel');
const profSaveBtn = document.getElementById('profile-save');
const profTitle = document.getElementById('profile-modal-title');
const profLabel = document.getElementById('profile-modal-label');

let currentField = null;
let inputEl = null;

// Функциональность редактирования профиля
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

// Функциональность модального окна пользователя 
const userModal = document.getElementById('user-info-modal');
const userCloseBtn = document.getElementById('user-info-close');
const userEditBtn = document.getElementById('user-edit-btn');
const userDeleteBtn = document.getElementById('user-delete-btn');

// Функциональность модального окна товара
const productModal = document.getElementById('product-info-modal');
const productCloseBtn = document.getElementById('product-info-close');
const productEditBtn = document.getElementById('product-edit-btn');
const productDeleteBtn = document.getElementById('product-delete-btn');

let currentUserData = null;
let currentProductData = null;

// Обработчики событий для модального окна пользователя
userCloseBtn.addEventListener('click', hideUserModal);
userEditBtn.addEventListener('click', editUser);
userDeleteBtn.addEventListener('click', deleteUser);

// Закрыть модальное окно пользователя при клике на фон
userModal.addEventListener('click', (e) => {
    if (e.target === userModal) {
        hideUserModal();
    }
});

// Обработчики событий для модального окна товара
productCloseBtn.addEventListener('click', hideProductModal);
productEditBtn.addEventListener('click', editProduct);
productDeleteBtn.addEventListener('click', deleteProduct);

// Закрыть модальное окно товара при клике на фон
productModal.addEventListener('click', (e) => {
    if (e.target === productModal) {
        hideProductModal();
    }
});

function openUserModal(userData) {
    currentUserData = userData;

    // Установка содержимого модального окна пользователя с использованием правильных элементов
    document.getElementById('user-info-image').src = '/frontend/img/profile/main/profile/profile avatar.png';
    document.getElementById('user-info-image').alt = userData.userName || 'User';
    document.getElementById('user-info-name').textContent = userData.userName || 'Без имени';
    document.getElementById('user-info-email').textContent = userData.userEmail || 'Без email';
    
    // Показать ID пользователя
    const idElement = document.getElementById('user-info-id');
    idElement.innerHTML = `ID пользователя: <span>${userData.userId || 'N/A'}</span>`;
    
    // Показать дату создания
    const createdElement = document.getElementById('user-info-created');
    createdElement.innerHTML = `Дата создания: <span>${userData.userCreated ? new Date(userData.userCreated).toLocaleDateString() : 'Неизвестно'}</span>`;

    userModal.classList.remove('hidden');
    userModal.setAttribute('aria-hidden', 'false');
}

function hideUserModal() {
    userModal.classList.add('hidden');
    userModal.setAttribute('aria-hidden', 'true');
    currentUserData = null;
}

async function editUser() {
    if (!currentUserData || !currentUserData.userId) {
        Toast.error('Невозможно изменить роль пользователя');
        return;
    }

    const newRole = currentUserData.userRole === 'USER' ? 'ADMIN' : 'USER';

    if (!confirm(`Изменить роль пользователя "${currentUserData.userName}" на ${newRole}?`)) {
        return;
    }

    try {
        await ApiHelper.patch(`/api/admin/users/${currentUserData.userId}/role`, {
            role: newRole
        }, {
            showSuccessToast: true,
            successMessage: `Роль пользователя изменена на ${newRole}`
        });

        // Перезагрузить список пользователей
        const users = await ApiHelper.get('/api/admin/users');
        renderUsers(users);
        hideUserModal();

    } catch (e) {
        console.error('Failed to update user role:', e);
    }
}

async function deleteUser() {
    if (!currentUserData || !currentUserData.userId) {
        Toast.error('Невозможно удалить пользователя');
        return;
    }

    if (!confirm(`Вы уверены, что хотите удалить пользователя "${currentUserData.userName}"? Это действие нельзя отменить.`)) {
        return;
    }

    try {
        await ApiHelper.delete(`/api/admin/users/${currentUserData.userId}`, {
            showSuccessToast: true,
            successMessage: 'Пользователь удален'
        });

        // Перезагрузить список пользователей
        const users = await ApiHelper.get('/api/admin/users');
        renderUsers(users);
        hideUserModal();

    } catch (e) {
        console.error('Failed to delete user:', e);
    }
}

// Функциональность модального окна товара 
function openProductModal(productData) {
    currentProductData = productData;

    // Установка содержимого модального окна товара
    document.getElementById('product-info-image').src = productData.productImage || '/frontend/img/profile/main/profile/profile avatar.png';
    document.getElementById('product-info-image').alt = productData.productName || 'Product';
    document.getElementById('product-info-name').textContent = productData.productName || 'Без названия';
    document.getElementById('product-info-price').textContent = `${productData.productPrice || 0} ₽`;
    
    // Показать поля специфичные для товара
    const quantityElement = document.getElementById('product-info-quantity');
    const stockStatus = productData.productQuantity > 0 ? 'В наличии' : 'Нет в наличии';
    quantityElement.innerHTML = `Количество: <span class="${productData.productQuantity > 0 ? 'in-stock' : 'out-of-stock'}">${productData.productQuantity}</span>`;
    
    const totalElement = document.getElementById('product-info-total');
    totalElement.innerHTML = `Статус: <span class="${productData.productQuantity > 0 ? 'in-stock' : 'out-of-stock'}">${stockStatus}</span>`;

    productModal.classList.remove('hidden');
    productModal.setAttribute('aria-hidden', 'false');
}

function hideProductModal() {
    productModal.classList.add('hidden');
    productModal.setAttribute('aria-hidden', 'true');
    currentProductData = null;
}

async function editProduct() {
    if (!currentProductData || !currentProductData.productId) {
        Toast.error('Невозможно редактировать товар');
        return;
    }

    // Сохраняем данные товара перед закрытием модалки
    const productData = { ...currentProductData };

    // Закрываем информационную модалку
    hideProductModal();

    // Заполняем поля модалки редактирования используя сохраненные данные
    document.getElementById('edit-product-name').value = productData.productName || '';
    document.getElementById('edit-product-price').value = productData.productPrice || '';
    document.getElementById('edit-product-quantity').value = productData.productQuantity || '';
    document.getElementById('edit-product-image').value = productData.productImage || '';

    // Сохраняем данные для использования при сохранении изменений
    currentProductData = productData;

    // Открываем модалку редактирования
    const editProductModal = document.getElementById('edit-product-modal');
    editProductModal.classList.remove('hidden');
    editProductModal.setAttribute('aria-hidden', 'false');
}

async function deleteProduct() {
    if (!currentProductData || !currentProductData.productId) {
        Toast.error('Невозможно удалить товар');
        return;
    }

    if (!confirm(`Вы уверены, что хотите удалить товар "${currentProductData.productName}"? Это действие нельзя отменить.`)) {
        return;
    }

    try {
        await ApiHelper.delete(`/api/admin/products/${currentProductData.productId}`, {
            showSuccessToast: true,
            successMessage: 'Товар удален'
        });

        // Перезагрузить список товаров
        const products = await ApiHelper.get('/api/admin/products');
        renderProducts(products);
        hideProductModal();

    } catch (e) {
        console.error('Failed to delete product:', e);
        hideProductModal();  // Закрываем модалку даже при ошибке
    }
}

// Функциональность модального окна редактирования (может быть переназначено для редактирования пользователя)
const editModal = document.getElementById('product-edit-modal');
const editCancelBtn = document.getElementById('edit-cancel-btn');
const editSaveBtn = document.getElementById('edit-save-btn');
const editQtyInput = document.getElementById('edit-qty-input');
const editTotalPrice = document.getElementById('edit-total-price');

// Обработчики событий для модального окна редактирования
editCancelBtn.addEventListener('click', hideEditModalUser);
editSaveBtn.addEventListener('click', saveEditedUser);
editQtyInput.addEventListener('input', validateEditUser);

// Закрыть модальное окно редактирования при клике на фон
editModal.addEventListener('click', (e) => {
    if (e.target === editModal) {
        hideEditModalUser();
    }
});

function hideEditModalUser() {
    editModal.classList.add('hidden');
    editModal.setAttribute('aria-hidden', 'true');
}

function validateEditUser() {
    // Добавить логику валидации для редактирования пользователя, если нужно
    const isValid = editQtyInput.value.trim().length > 0;
    editSaveBtn.disabled = !isValid;
}

async function saveEditedUser() {
    // Добавить логику редактирования пользователя здесь
    Toast.info('Функция редактирования пользователя в разработке');
    hideEditModalUser();
}

// =====================================================
// Функциональность редактирования товаров
// =====================================================

// Получаем элементы модалки редактирования товара
const editProductModal = document.getElementById('edit-product-modal');
const editProductCancelBtn = document.getElementById('edit-product-cancel');
const editProductSaveBtn = document.getElementById('edit-product-save');
const editProductNameInput = document.getElementById('edit-product-name');
const editProductPriceInput = document.getElementById('edit-product-price');
const editProductQuantityInput = document.getElementById('edit-product-quantity');
const editProductImageInput = document.getElementById('edit-product-image');

// Обработчики событий для модалки редактирования товара
editProductCancelBtn.addEventListener('click', hideEditProductModal);
editProductSaveBtn.addEventListener('click', saveEditedProduct);

// Валидация полей при вводе
editProductNameInput.addEventListener('input', validateEditProductFields);
editProductPriceInput.addEventListener('input', validateEditProductFields);
editProductQuantityInput.addEventListener('input', validateEditProductFields);
editProductImageInput.addEventListener('input', validateEditProductFields);

// Закрытие модалки при клике на фон
editProductModal.addEventListener('click', (e) => {
    if (e.target === editProductModal) {
        hideEditProductModal();
    }
});

function hideEditProductModal() {
    editProductModal.classList.add('hidden');
    editProductModal.setAttribute('aria-hidden', 'true');
}

function validateEditProductFields() {
    // Убираем нецифровые символы из цены (разрешаем точку для дробной части) и количества
    editProductPriceInput.value = editProductPriceInput.value.replace(/[^\d.]/g, '');
    editProductQuantityInput.value = editProductQuantityInput.value.replace(/[^0-9]/g, '');

    const name = editProductNameInput.value.trim();
    const price = parseFloat(editProductPriceInput.value) || 0;
    const quantity = parseInt(editProductQuantityInput.value) || 0;
    const imagePath = editProductImageInput.value.trim();

    // Проверяем что все обязательные поля заполнены корректно
    // imagePath - не обязательное поле
    const isValid = name.length > 0 && price > 0 && quantity >= 0;

    editProductSaveBtn.disabled = !isValid;
}

async function saveEditedProduct() {
    if (!currentProductData || !currentProductData.productId) {
        Toast.error('Невозможно сохранить изменения');
        return;
    }

    const updatedData = {
        name: editProductNameInput.value.trim(),
        price: parseFloat(editProductPriceInput.value), // Используем parseFloat для BigDecimal
        quantity: parseInt(editProductQuantityInput.value),
        imagePath: editProductImageInput.value.trim() || '' // пустая строка если пустое
    };

    try {
        await ApiHelper.put(`/api/admin/products/${currentProductData.productId}`, updatedData, {
            showSuccessToast: true,
            successMessage: 'Товар успешно обновлен'
        });

        // Перезагружаем список товаров
        const products = await ApiHelper.get('/api/admin/products');
        renderProducts(products);

        hideEditProductModal();
    } catch (e) {
        console.error('Failed to update product:', e);
        // ApiHelper уже показал Toast с ошибкой
    }
}

// =====================================================
// Функциональность добавления новых товаров и пользователей
// =====================================================

// Элементы модального окна добавления товара
const addProductModal = document.getElementById('add-product-modal');
const addProductCancelBtn = document.getElementById('add-product-cancel');
const addProductConfirmBtn = document.getElementById('add-product-confirm');
const newProductNameInput = document.getElementById('new-product-name');
const newProductPriceInput = document.getElementById('new-product-price');
const newProductQuantityInput = document.getElementById('new-product-quantity');
const newProductImageInput = document.getElementById('new-product-image');

// Обработчики для модалки добавления товара
addProductCancelBtn.addEventListener('click', hideAddProductModal);
addProductConfirmBtn.addEventListener('click', createNewProduct);
newProductNameInput.addEventListener('input', validateProductInputs);
newProductPriceInput.addEventListener('input', validateProductInputs);
newProductQuantityInput.addEventListener('input', validateProductInputs);

// Обработчик для кнопки "нет товаров"
const noProductsBtn = document.getElementById('no-products');
noProductsBtn.addEventListener('click', openAddProductModal);
// Убираем обработчик для no-users - блок больше не кликабельный
addBtnEmpty.removeEventListener('click', openModal);

// Закрытие модалки при клике на фон
addProductModal.addEventListener('click', (e) => {
    if (e.target === addProductModal) {
        hideAddProductModal();
    }
});

// Функции для модалки добавления товара
function openAddProductModal() {
    // Очищаем поля
    newProductNameInput.value = '';
    newProductPriceInput.value = '';
    newProductQuantityInput.value = '';
    newProductImageInput.value = '';

    // Сбрасываем валидацию
    addProductConfirmBtn.disabled = true;

    // Показываем модалку
    addProductModal.classList.remove('hidden');
    addProductModal.setAttribute('aria-hidden', 'false');
    newProductNameInput.focus();
}

function hideAddProductModal() {
    addProductModal.classList.add('hidden');
    addProductModal.setAttribute('aria-hidden', 'true');
}

function validateProductInputs() {
    // Очищаем нецифровые символы для цены и количества
    newProductPriceInput.value = newProductPriceInput.value.replace(/[^\d.]/g, '');
    newProductQuantityInput.value = newProductQuantityInput.value.replace(/\D/g, '');

    const name = newProductNameInput.value.trim();
    const price = parseFloat(newProductPriceInput.value) || 0;
    const quantity = parseInt(newProductQuantityInput.value) || 0;

    // Валидация: название не пустое, цена > 0, количество >= 0
    const isValid = name.length > 0 && price > 0 && quantity >= 0;
    addProductConfirmBtn.disabled = !isValid;
}

async function createNewProduct() {
    const productData = {
        name: newProductNameInput.value.trim(),
        price: parseFloat(newProductPriceInput.value),
        quantity: parseInt(newProductQuantityInput.value),
        imagePath: newProductImageInput.value.trim() || ''
    };

    try {
        await ApiHelper.post('/api/admin/products', productData, {
            showSuccessToast: true,
            successMessage: 'Товар успешно добавлен'
        });

        // Перезагружаем список товаров
        const products = await ApiHelper.get('/api/admin/products');
        renderProducts(products);
        hideAddProductModal();

    } catch (e) {
        console.error('Failed to create product:', e);
        // ApiHelper уже показал Toast с ошибкой
    }
}