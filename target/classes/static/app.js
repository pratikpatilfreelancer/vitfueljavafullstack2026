let cart = [];
let total = 0;

async function loadFood() {
    const container = document.getElementById("foodContainer");

    try {
        const response = await fetch("/api/food");

        if (!response.ok) {
            throw new Error("Failed to load food");
        }

        const foods = await response.json();

        container.innerHTML = "";

        if (!foods.length) {
            container.innerHTML = "<p>No food items available.</p>";
            return;
        }

       
        const uniqueFoods = foods.filter(
            (food, index, self) =>
                index === self.findIndex(f => f.name === food.name)
        );

        // Different image for every food item
        const foodImages = {

            "Margherita Pizza":
                "https://images.unsplash.com/photo-1574071318508-1cdbab80d002",

            "Pepperoni Pizza":
                "https://images.unsplash.com/photo-1628840042765-356cda07504e",

            "Veg Burger":
                "https://images.unsplash.com/photo-1520072959219-c595dc870360",

            "Chicken Burger":
                "https://images.unsplash.com/photo-1568901346375-23c9450c58cd",

            "French Fries":
                "https://images.unsplash.com/photo-1573080496219-bb080dd4f877",

            "Butter Chicken":
                "https://images.unsplash.com/photo-1603894584373-5ac82b2ae398",

            "Paneer Tikka":
                "https://images.unsplash.com/photo-1567188040759-fb8a883dc6d8",

            "Biryani":
                "https://images.unsplash.com/photo-1563379091339-03246963d51a",

            "Raita":
                "https://images.unsplash.com/photo-1596797038530-2c107229654b",

            "Noodles":
                "https://images.unsplash.com/photo-1585032226651-759b368d7246",

            "Manchow Soup":
                "https://images.unsplash.com/photo-1547592166-23ac45744acd",

            "Spring Roll":
                "https://images.unsplash.com/photo-1548507200-dbd9a7c7b4b4",

            "Coke":
                "https://images.unsplash.com/photo-1629203849820-fdd70d49c38e",

            "Cold Drink":
                "https://images.unsplash.com/photo-1554866585-cd94860890b7",

            "Coffee":
                "https://images.unsplash.com/photo-1495474472287-4d71bcdd2085",

            "Brownie":
                "https://images.unsplash.com/photo-1564355808539-22fda35bed7e",

            "Ice Cream":
                "https://images.unsplash.com/photo-1563805042-7684c019e1cb"
        };

        
        uniqueFoods.forEach(food => {

            const card = document.createElement("div");
            card.className = "food-card";

            const image =
                foodImages[food.name] ||
                "https://images.unsplash.com/photo-1504674900247-0877df9cc836";

            card.innerHTML = `
                <img
                    src="${image}"
                    alt="${food.name}"
                >

                <div class="food-info">

                    <h3>${food.name}</h3>

                    <p>
                        ${food.description || "Delicious food"}
                    </p>

                    <div class="price">
                        ₹${food.price}
                    </div>

                    <button class="add-btn">
                        Add to Cart
                    </button>

                </div>
            `;

            // Add food to cart
            card.querySelector(".add-btn").addEventListener("click", () => {
                addToCart(food);
            });

            container.appendChild(card);
        });

    } catch (error) {

        console.error("Error loading food:", error);

        container.innerHTML =
            "<p>Unable to load food items.</p>";
    }
}



function addToCart(food) {

    cart.push(food);

    total += Number(food.price);

    updateCart();
}



function updateCart() {

    const items = document.getElementById("cartItems");
    const cartTotal = document.getElementById("cartTotal");

    if (!items || !cartTotal) {
        return;
    }

    cartTotal.textContent = total.toFixed(2);

    items.innerHTML = "";

    cart.forEach((food, index) => {

        const item = document.createElement("div");

        item.innerHTML = `
            <p>
                ${food.name} - ₹${food.price}
                <button onclick="removeItem(${index})">
                    X
                </button>
            </p>
        `;

        items.appendChild(item);
    });
}

function removeItem(index) {

    if (index < 0 || index >= cart.length) {
        return;
    }

    total -= Number(cart[index].price);

    cart.splice(index, 1);

    updateCart();
}

async function placeOrder() {

    if (!cart.length) {
        alert("Your cart is empty.");
        return;
    }

    try {

        const orderData = {
            customerId: 1,
            paymentMethod: "CASH",

            orderItems: cart.map(food => ({
                foodId: food.id,
                foodName: food.name,
                quantity: 1,
                price: Number(food.price)
            }))
        };

        console.log("Sending order:", orderData);

        const response = await fetch("/api/orders", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(orderData)
        });

        const result = await response.json();

        console.log("Server response:", result);

        if (!response.ok) {
            throw new Error(
                result.message || "Unable to place order"
            );
        }

        alert(
            "Order placed successfully!\nOrder ID: " +
            result.orderId
        );

        // Clear cart
        cart = [];
        total = 0;

        updateCart();

    } catch (error) {

        console.error("Order error:", error);

        alert("Unable to place order: " + error.message);
    }
}

function scrollToMenu() {

    const menu = document.getElementById("menu");

    if (menu) {
        menu.scrollIntoView({
            behavior: "smooth"
        });
    }
}

function showLogin() {

    const modal = document.getElementById("loginModal");

    if (modal) {
        modal.style.display = "block";
    }
}


function closeLogin() {

    const modal = document.getElementById("loginModal");

    if (modal) {
        modal.style.display = "none";
    }
}



async function login() {

    const emailElement =
        document.getElementById("loginEmail");

    const passwordElement =
        document.getElementById("loginPassword");

    const messageElement =
        document.getElementById("loginMessage");

    if (!emailElement || !passwordElement) {
        return;
    }

    const email = emailElement.value.trim();

    const password = passwordElement.value;

    if (!email || !password) {

        if (messageElement) {
            messageElement.textContent =
                "Please enter email and password.";
        }

        return;
    }

    try {

        const response = await fetch("/api/auth/login", {

            method: "POST",

            headers: {
                "Content-Type": "application/json"
            },

            body: JSON.stringify({
                email: email,
                password: password
            })
        });

        const result = await response.json();

        if (messageElement) {

            messageElement.textContent =
                response.ok
                    ? "Login successful!"
                    : (result.message || "Login failed.");
        }

    } catch (error) {

        console.error("Login error:", error);

        if (messageElement) {

            messageElement.textContent =
                "Unable to connect to server.";
        }
    }
}

document.addEventListener(
    "DOMContentLoaded",
    loadFood
);