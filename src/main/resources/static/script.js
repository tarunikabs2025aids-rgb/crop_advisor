const API = "/api";

let regions = [];
let farmers = [];
let officers = [];
let tickets = [];

document.addEventListener("DOMContentLoaded", () => {
    setupForms();
    loadAllData();
});

function showSection(sectionId) {
    document.querySelectorAll(".section").forEach(section => {
        section.classList.remove("active");
    });

    document.getElementById(sectionId).classList.add("active");

    loadAllData();
}

function setupForms() {

    document.getElementById("regionForm").addEventListener("submit", async event => {
        event.preventDefault();

        await sendRequest("/regions", "POST", {
            name: document.getElementById("regionName").value.trim()
        });

        event.target.reset();
        await loadAllData();
    });

    document.getElementById("farmerForm").addEventListener("submit", async event => {
        event.preventDefault();

        await sendRequest("/farmers", "POST", {
            name: document.getElementById("farmerName").value.trim(),
            email: document.getElementById("farmerEmail").value.trim(),
            phone: document.getElementById("farmerPhone").value.trim(),
            regionId: Number(document.getElementById("farmerRegionId").value)
        });

        event.target.reset();
        await loadAllData();
    });

    document.getElementById("officerForm").addEventListener("submit", async event => {
        event.preventDefault();

        await sendRequest("/officers", "POST", {
            name: document.getElementById("officerName").value.trim(),
            email: document.getElementById("officerEmail").value.trim(),
            phone: document.getElementById("officerPhone").value.trim(),
            regionId: Number(document.getElementById("officerRegionId").value),
            active: document.getElementById("officerActive").checked
        });

        event.target.reset();
        document.getElementById("officerActive").checked = true;

        await loadAllData();
    });

    document.getElementById("ticketForm").addEventListener("submit", async event => {
        event.preventDefault();

        await sendRequest("/tickets", "POST", {
            farmerId: Number(document.getElementById("ticketFarmerId").value),
            crop: document.getElementById("ticketCrop").value.trim(),
            symptoms: document.getElementById("ticketSymptoms").value.trim(),
            photoReference: document.getElementById("ticketPhotoReference").value.trim()
        });

        event.target.reset();
        await loadAllData();
    });
}

async function loadAllData() {
    try {

        const [
            regionData,
            farmerData,
            officerData,
            ticketData
        ] = await Promise.all([
            fetchJson("/regions"),
            fetchJson("/farmers"),
            fetchJson("/officers"),
            fetchJson("/tickets")
        ]);

        regions = regionData;
        farmers = farmerData;
        officers = officerData;
        tickets = ticketData;

        renderRegions();
        renderFarmers();
        renderOfficers();
        renderTicketForm();
        renderTickets();
        updateDashboard();

    } catch (error) {
        showMessage(error.message, "error");
    }
}

/* =========================
   REGIONS
========================= */

function renderRegions() {

    const body = document.getElementById("regionTableBody");

    if (regions.length === 0) {
        body.innerHTML = emptyRow(3, "No regions found.");
        return;
    }

    body.innerHTML = regions.map(region => `
        <tr>
            <td>${region.id}</td>

            <td>
                ${escapeHtml(region.name)}
            </td>

            <td>
                <button
                    class="action-button edit-button"
                    onclick="editRegion(${region.id})">
                    Edit
                </button>

                <button
                    class="action-button delete-button"
                    onclick="deleteRegion(${region.id})">
                    Delete
                </button>
            </td>
        </tr>
    `).join("");

    const farmerSelect = document.getElementById("farmerRegionId");
    const officerSelect = document.getElementById("officerRegionId");

    farmerSelect.innerHTML =
        `<option value="">Select Region</option>` +
        regions.map(region =>
            `<option value="${region.id}">
                ${escapeHtml(region.name)}
            </option>`
        ).join("");

    officerSelect.innerHTML =
        `<option value="">Select Region</option>` +
        regions.map(region =>
            `<option value="${region.id}">
                ${escapeHtml(region.name)}
            </option>`
        ).join("");
}

async function editRegion(id) {

    const region = regions.find(item => item.id === id);

    if (!region) {
        return;
    }

    const newName = prompt(
        "Enter Region Name:",
        region.name
    );

    if (newName === null) {
        return;
    }

    if (!newName.trim()) {
        showMessage("Region name is required.", "error");
        return;
    }

    await sendRequest(`/regions/${id}`, "PUT", {
        name: newName.trim()
    });

    await loadAllData();
}

async function deleteRegion(id) {

    const region = regions.find(item => item.id === id);

    if (!region) {
        return;
    }

    const confirmed = confirm(
        `Are you sure you want to delete Region #${id} (${region.name})?`
    );

    if (!confirmed) {
        return;
    }

    await sendRequest(`/regions/${id}`, "DELETE");

    await loadAllData();
}

/* =========================
   FARMERS
========================= */

function renderFarmers() {

    const body = document.getElementById("farmerTableBody");

    if (farmers.length === 0) {
        body.innerHTML = emptyRow(6, "No farmers found.");
        return;
    }

    body.innerHTML = farmers.map(farmer => `
        <tr>
            <td>${farmer.id}</td>

            <td>
                ${escapeHtml(farmer.name)}
            </td>

            <td>
                ${escapeHtml(farmer.email)}
            </td>

            <td>
                ${escapeHtml(farmer.phone)}
            </td>

            <td>
                ${escapeHtml(farmer.region?.name ?? "-")}
            </td>

            <td>
                <button
                    class="action-button edit-button"
                    onclick="editFarmer(${farmer.id})">
                    Edit
                </button>

                <button
                    class="action-button delete-button"
                    onclick="deleteFarmer(${farmer.id})">
                    Delete
                </button>
            </td>
        </tr>
    `).join("");
}

async function editFarmer(id) {

    const farmer = farmers.find(item => item.id === id);

    if (!farmer) {
        return;
    }

    const name = prompt(
        "Enter Farmer Name:",
        farmer.name
    );

    if (name === null) {
        return;
    }

    const email = prompt(
        "Enter Email:",
        farmer.email
    );

    if (email === null) {
        return;
    }

    const phone = prompt(
        "Enter Phone Number:",
        farmer.phone
    );

    if (phone === null) {
        return;
    }

    const regionId = prompt(
        "Enter Region ID:",
        farmer.region?.id ?? ""
    );

    if (regionId === null) {
        return;
    }

    if (!name.trim() || !email.trim() || !phone.trim() || !regionId) {
        showMessage("All farmer details are required.", "error");
        return;
    }

    await sendRequest(`/farmers/${id}`, "PUT", {
        name: name.trim(),
        email: email.trim(),
        phone: phone.trim(),
        regionId: Number(regionId)
    });

    await loadAllData();
}

async function deleteFarmer(id) {

    const farmer = farmers.find(item => item.id === id);

    if (!farmer) {
        return;
    }

    const confirmed = confirm(
        `Are you sure you want to delete Farmer #${id} (${farmer.name})?`
    );

    if (!confirmed) {
        return;
    }

    await sendRequest(`/farmers/${id}`, "DELETE");

    await loadAllData();
}

/* =========================
   OFFICERS
========================= */

function renderOfficers() {

    const body = document.getElementById("officerTableBody");

    if (officers.length === 0) {
        body.innerHTML = emptyRow(7, "No officers found.");
        return;
    }

    body.innerHTML = officers.map(officer => `
        <tr>
            <td>${officer.id}</td>

            <td>
                ${escapeHtml(officer.name)}
            </td>

            <td>
                ${escapeHtml(officer.email)}
            </td>

            <td>
                ${escapeHtml(officer.phone)}
            </td>

            <td>
                ${escapeHtml(officer.region?.name ?? "-")}
            </td>

            <td>
                <span class="${officer.active ? "status-open" : "status-closed"}">
                    ${officer.active ? "ACTIVE" : "INACTIVE"}
                </span>
            </td>

            <td>
                <button
                    class="action-button edit-button"
                    onclick="editOfficer(${officer.id})">
                    Edit
                </button>

                <button
                    class="action-button delete-button"
                    onclick="deleteOfficer(${officer.id})">
                    Delete
                </button>
            </td>
        </tr>
    `).join("");
}

async function editOfficer(id) {

    const officer = officers.find(item => item.id === id);

    if (!officer) {
        return;
    }

    const name = prompt(
        "Enter Officer Name:",
        officer.name
    );

    if (name === null) {
        return;
    }

    const email = prompt(
        "Enter Email:",
        officer.email
    );

    if (email === null) {
        return;
    }

    const phone = prompt(
        "Enter Phone Number:",
        officer.phone
    );

    if (phone === null) {
        return;
    }

    const regionId = prompt(
        "Enter Region ID:",
        officer.region?.id ?? ""
    );

    if (regionId === null) {
        return;
    }

    const active = confirm(
        "Should this officer be ACTIVE?"
    );

    if (!name.trim() || !email.trim() || !phone.trim() || !regionId) {
        showMessage("All officer details are required.", "error");
        return;
    }

    await sendRequest(`/officers/${id}`, "PUT", {
        name: name.trim(),
        email: email.trim(),
        phone: phone.trim(),
        regionId: Number(regionId),
        active: active
    });

    await loadAllData();
}

async function deleteOfficer(id) {

    const officer = officers.find(item => item.id === id);

    if (!officer) {
        return;
    }

    const confirmed = confirm(
        `Are you sure you want to delete Officer #${id} (${officer.name})?`
    );

    if (!confirmed) {
        return;
    }

    await sendRequest(`/officers/${id}`, "DELETE");

    await loadAllData();
}

/* =========================
   TICKET FORM
========================= */

function renderTicketForm() {

    const select = document.getElementById("ticketFarmerId");

    select.innerHTML =
        `<option value="">Select Farmer</option>` +
        farmers.map(farmer =>
            `<option value="${farmer.id}">
                ${escapeHtml(farmer.name)}
                - ${escapeHtml(farmer.region?.name ?? "")}
            </option>`
        ).join("");
}

/* =========================
   TICKETS
========================= */

function renderTickets() {

    const body = document.getElementById("ticketTableBody");

    if (tickets.length === 0) {
        body.innerHTML = emptyRow(12, "No advisory tickets found.");
        return;
    }

    body.innerHTML = tickets.map(ticket => {

        const escalated = isEscalated(ticket);

        return `
            <tr>

                <td>
                    ${ticket.id}
                </td>

                <td>
                    ${escapeHtml(ticket.farmer?.name ?? "-")}
                    <small>
                        ID: ${ticket.farmer?.id ?? "-"}
                    </small>
                </td>

                <td>
                    ${escapeHtml(ticket.farmer?.region?.name ?? "-")}
                </td>

                <td>
                    ${escapeHtml(ticket.crop)}
                </td>

                <td class="symptoms-cell">
                    ${escapeHtml(ticket.symptoms)}
                </td>

                <td>
                    ${
            ticket.photoReference
                ? escapeHtml(ticket.photoReference)
                : "-"
        }
                </td>

                <td>
                    ${escapeHtml(ticket.assignedOfficer?.name ?? "-")}
                    <small>
                        ID: ${ticket.assignedOfficer?.id ?? "-"}
                    </small>
                </td>

                <td>
                    <span class="${
            ticket.status === "CLOSED"
                ? "status-closed"
                : "status-open"
        }">
                        ${escapeHtml(ticket.status)}
                    </span>
                </td>

                <td>
                    ${formatTimer(ticket.timerStartedAt)}
                </td>

                <td>
                    ${
            escalated
                ? '<span class="escalated-badge">ESCALATED</span>'
                : '<span class="normal-badge">Within 48h</span>'
        }
                </td>

                <td class="recommendation-cell">
                    ${
            ticket.recommendation
                ? escapeHtml(ticket.recommendation)
                : "-"
        }
                </td>

                <td>

                    <button
                        class="action-button edit-button"
                        onclick="editTicket(${ticket.id})">
                        Edit
                    </button>

                    <button
                        class="action-button delete-button"
                        onclick="deleteTicket(${ticket.id})">
                        Delete
                    </button>

                    ${
            ticket.status !== "CLOSED"
                ? `
                                <button
                                    class="action-button close-button"
                                    onclick="closeTicket(
                                        ${ticket.id},
                                        ${ticket.assignedOfficer?.id ?? "null"}
                                    )">
                                    Close & Recommend
                                </button>
                            `
                : `
                                <button
                                    class="action-button reopen-button"
                                    onclick="reopenTicket(
                                        ${ticket.id},
                                        ${ticket.farmer?.id ?? "null"}
                                    )">
                                    Reopen
                                </button>
                            `
        }

                </td>

            </tr>
        `;

    }).join("");
}

async function editTicket(id) {

    const ticket = tickets.find(item => item.id === id);

    if (!ticket) {
        return;
    }

    const crop = prompt(
        "Enter Crop Name:",
        ticket.crop
    );

    if (crop === null) {
        return;
    }

    const symptoms = prompt(
        "Enter Symptoms:",
        ticket.symptoms
    );

    if (symptoms === null) {
        return;
    }

    const photoReference = prompt(
        "Enter Photo Reference:",
        ticket.photoReference ?? ""
    );

    if (photoReference === null) {
        return;
    }

    if (!crop.trim() || !symptoms.trim()) {
        showMessage(
            "Crop and symptoms are required.",
            "error"
        );
        return;
    }

    await sendRequest(`/tickets/${id}`, "PUT", {
        crop: crop.trim(),
        symptoms: symptoms.trim(),
        photoReference: photoReference.trim()
    });

    await loadAllData();
}

async function deleteTicket(id) {

    const ticket = tickets.find(item => item.id === id);

    if (!ticket) {
        return;
    }

    const confirmed = confirm(
        `Are you sure you want to delete Ticket #${id}?`
    );

    if (!confirmed) {
        return;
    }

    await sendRequest(`/tickets/${id}`, "DELETE");

    await loadAllData();
}

/* =========================
   CLOSE TICKET
========================= */

async function closeTicket(ticketId, officerId) {

    const ticket = tickets.find(item => item.id === ticketId);

    if (!ticket) {
        return;
    }

    if (!officerId) {
        showMessage(
            "No assigned officer found for this ticket.",
            "error"
        );
        return;
    }

    const recommendation = prompt(
        `Enter recommendation for Ticket #${ticketId}:`
    );

    if (recommendation === null) {
        return;
    }

    if (!recommendation.trim()) {
        showMessage(
            "Recommendation is required.",
            "error"
        );
        return;
    }

    const confirmed = confirm(
        `Close Ticket #${ticketId} as Officer #${officerId}?`
    );

    if (!confirmed) {
        return;
    }

    await sendRequest(
        `/tickets/${ticketId}/close`,
        "PUT",
        {
            officerId: officerId,
            recommendation: recommendation.trim()
        }
    );

    await loadAllData();
}

/* =========================
   REOPEN TICKET
========================= */

async function reopenTicket(ticketId, farmerId) {

    if (!farmerId) {
        showMessage(
            "Ticket farmer information is missing.",
            "error"
        );
        return;
    }

    const confirmed = confirm(
        `Reopen Ticket #${ticketId} as Farmer #${farmerId}?\n\n` +
        `The 48-hour resolution timer will start again from now.`
    );

    if (!confirmed) {
        return;
    }

    await sendRequest(
        `/tickets/${ticketId}/reopen?farmerId=${farmerId}`,
        "PUT"
    );

    await loadAllData();
}

/* =========================
   DASHBOARD
========================= */

function updateDashboard() {

    document.getElementById("totalRegions").textContent =
        regions.length;

    document.getElementById("totalFarmers").textContent =
        farmers.length;

    document.getElementById("totalOfficers").textContent =
        officers.length;

    document.getElementById("totalTickets").textContent =
        tickets.length;

    const openCount =
        tickets.filter(ticket =>
            ticket.status !== "CLOSED"
        ).length;

    const escalatedCount =
        tickets.filter(ticket =>
            isEscalated(ticket)
        ).length;

    document.getElementById("openTickets").textContent =
        openCount;

    document.getElementById("escalatedTickets").textContent =
        escalatedCount;
}

/* =========================
   HTTP REQUEST
========================= */

async function sendRequest(
    endpoint,
    method,
    body = null
) {

    try {

        const options = {
            method: method,
            headers: {
                "Content-Type": "application/json"
            }
        };

        if (body !== null) {
            options.body = JSON.stringify(body);
        }

        const response =
            await fetch(API + endpoint, options);

        const data =
            await response.json().catch(() => ({}));

        if (!response.ok) {

            throw new Error(
                data.message ||
                "Request failed. Please check the entered details."
            );
        }

        showMessage(
            "Operation completed successfully.",
            "success"
        );

        return data;

    } catch (error) {

        showMessage(
            error.message,
            "error"
        );

        throw error;
    }
}

async function fetchJson(endpoint) {

    const response =
        await fetch(API + endpoint);

    const data =
        await response.json().catch(() => ({}));

    if (!response.ok) {

        throw new Error(
            data.message ||
            "Unable to load data."
        );
    }

    return data;
}

/* =========================
   ESCALATION
========================= */

function isEscalated(ticket) {

    if (ticket.status === "CLOSED") {
        return false;
    }

    if (ticket.escalated === true) {
        return true;
    }

    const start =
        new Date(ticket.timerStartedAt).getTime();

    if (Number.isNaN(start)) {
        return false;
    }

    return Date.now() - start >=
        48 * 60 * 60 * 1000;
}

/* =========================
   TIMER
========================= */

function formatTimer(dateValue) {

    if (!dateValue) {
        return "-";
    }

    const start =
        new Date(dateValue).getTime();

    if (Number.isNaN(start)) {
        return "-";
    }

    const diff =
        Math.max(
            0,
            Date.now() - start
        );

    const totalHours =
        Math.floor(
            diff / (1000 * 60 * 60)
        );

    const days =
        Math.floor(totalHours / 24);

    const hours =
        totalHours % 24;

    return `${days}d ${hours}h`;
}

/* =========================
   MESSAGE
========================= */

function showMessage(
    message,
    type
) {

    const box =
        document.getElementById("messageBox");

    box.textContent = message;

    box.className =
        type === "success"
            ? "message-success"
            : "message-error";

    box.style.display = "block";

    setTimeout(() => {
        box.style.display = "none";
    }, 3500);
}

/* =========================
   EMPTY ROW
========================= */

function emptyRow(
    columnCount,
    message
) {

    return `
        <tr>
            <td
                colspan="${columnCount}"
                class="empty-cell">
                ${escapeHtml(message)}
            </td>
        </tr>
    `;
}

/* =========================
   HTML ESCAPE
========================= */

function escapeHtml(value) {

    if (
        value === null ||
        value === undefined
    ) {
        return "";
    }

    return String(value)
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}