// Общие утилиты корзины для единообразного поведения корзины во всем приложении
class CartUtils {
    // Перестроить опции выбора для выбора продукта
    static rebuildSelectOptions(selectElement, products, options = {}) {
        if (!selectElement) return;

        const {
            priceFormat = (price) => `${price} ₽`,
            quantityFormat = (quantity) => `(остаток ${quantity})`,
            nameFormat = (name, price, quantity) => `${name} — ${priceFormat(price)} ${quantityFormat(quantity)}`
        } = options;

        selectElement.innerHTML = '';

        products.forEach(product => {
            const text = nameFormat(product.name, product.price, product.quantity);
            selectElement.add(new Option(text, product.id));
        });
    }

    // Рассчитать и обновить отображение итоговой цены
    static updateTotalCalculation(params) {
        const {
            selectElement,
            quantityInput,
            totalPriceElement,
            products,
            submitButton,
            emptyText = 'Итого: —',
            formatTotal = (total) => `Итого: ${total.toLocaleString()} ₽`
        } = params;

        // Очистка ввода количества - удаление нецифровых символов
        if (quantityInput) {
            quantityInput.value = quantityInput.value.replace(/\D/g, '');
        }

        const productId = Number(selectElement?.value);
        const product = products?.find(p => p.id === productId);
        const quantity = parseInt(quantityInput?.value, 10) || 0;

        // Валидация
        if (!product || !quantity || quantity <= 0) {
            if (totalPriceElement) {
                totalPriceElement.textContent = emptyText;
            }
            if (submitButton) {
                submitButton.disabled = true;
            }
            return { isValid: false, total: 0, quantity: 0 };
        }

        // Применение ограничений количества
        const validQuantity = Math.min(Math.max(quantity, 1), product.quantity);

        if (quantityInput && validQuantity !== quantity) {
            quantityInput.value = validQuantity;
        }

        const total = product.price * validQuantity;

        // Обновление UI
        if (totalPriceElement) {
            totalPriceElement.textContent = formatTotal(total);
        }
        if (submitButton) {
            submitButton.disabled = false;
        }

        return {
            isValid: true,
            total,
            quantity: validQuantity,
            product
        };
    }

    // Обработка добавления товара в корзину через API вызов
    static async addProductToCart(params) {
        const {
            productId,
            quantity,
            apiEndpoint = '/api/cart',
            onSuccess = null,
            onError = null,
            successMessage = null
        } = params;

        if (!productId || !quantity) {
            throw new Error('Product ID and quantity are required');
        }

        try {
            const options = {
                showSuccessToast: !!successMessage,
                ...(successMessage && { successMessage })
            };

            const response = await ApiHelper.post(apiEndpoint, {
                id: productId,
                quantity
            }, options);

            if (onSuccess) {
                onSuccess(response);
            }

            return response;
        } catch (error) {
            if (onError) {
                onError(error);
            }
            throw error;
        }
    }

    // Обновление кэша продуктов после операции с корзиной
    static updateProductsCache(productsCache, productId, quantityUsed) {
        const product = productsCache.find(p => p.id === productId);
        if (!product) return productsCache;

        product.quantity -= quantityUsed;

        // Удалить продукт из кэша если нет в наличии
        if (product.quantity <= 0) {
            return productsCache.filter(p => p.id !== productId);
        }

        return productsCache;
    }

    // Универсальный контроллер модального окна корзины для обработки общих операций
    static createCartController(config) {
        const {
            modalId,
            selectElementId,
            quantityInputId,
            totalPriceElementId,
            submitButtonId,
            cancelButtonId,
            loadProductsEndpoint = '/api/cart',
            addToCartEndpoint = '/api/cart',
            onCartUpdate = null
        } = config;

        const modal = document.getElementById(modalId);
        const selectEl = document.getElementById(selectElementId);
        const qtyInput = document.getElementById(quantityInputId);
        const totalPriceEl = document.getElementById(totalPriceElementId);
        const submitBtn = document.getElementById(submitButtonId);
        const cancelBtn = document.getElementById(cancelButtonId);

        let productsCache = [];

        const updateTotal = () => {
            return this.updateTotalCalculation({
                selectElement: selectEl,
                quantityInput: qtyInput,
                totalPriceElement: totalPriceEl,
                products: productsCache,
                submitButton: submitBtn
            });
        };

        const openModal = async () => {
            // Загрузить продукты если кэш пустой
            if (productsCache.length === 0) {
                try {
                    const products = await ApiHelper.get(loadProductsEndpoint);
                    productsCache = products.filter(p => p.quantity > 0);
                    this.rebuildSelectOptions(selectEl, productsCache);
                } catch (error) {
                    console.error('Failed to load products:', error);
                    return;
                }
            }

            if (!productsCache.length) {
                Toast.info('Все товары распроданы');
                return;
            }

            // Сброс формы
            if (selectEl) selectEl.selectedIndex = 0;
            if (qtyInput) qtyInput.value = '';
            updateTotal();

            ModalUtils.showModal(modal, qtyInput);
        };

        const hideModal = () => {
            ModalUtils.hideModal(modal);
        };

        const submitCart = async () => {
            const result = updateTotal();
            if (!result.isValid) return;

            try {
                const cartData = await this.addProductToCart({
                    productId: result.product.id,
                    quantity: result.quantity,
                    apiEndpoint: addToCartEndpoint,
                    successMessage: `Товар добавлен в корзину (${result.quantity} шт.)`
                });

                // Обновить кэш
                productsCache = this.updateProductsCache(
                    productsCache,
                    result.product.id,
                    result.quantity
                );
                this.rebuildSelectOptions(selectEl, productsCache);

                if (onCartUpdate) {
                    onCartUpdate(cartData);
                }

                hideModal();
            } catch (error) {
                console.error('Failed to add to cart:', error);
            }
        };

        // Установка обработчиков событий
        if (selectEl) selectEl.addEventListener('change', updateTotal);
        if (qtyInput) qtyInput.addEventListener('input', updateTotal);
        if (submitBtn) submitBtn.addEventListener('click', submitCart);
        if (cancelBtn) cancelBtn.addEventListener('click', hideModal);

        // Настройка поведения модального окна
        ModalUtils.setupBackgroundClose(modal, hideModal);

        return {
            openModal,
            hideModal,
            updateTotal,
            submitCart,
            getProductsCache: () => [...productsCache],
            resetProductsCache: () => { productsCache = []; }
        };
    }
}

// Делаем утилиты глобально доступными
window.CartUtils = CartUtils;