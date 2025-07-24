$(document).ready(function() {
    if (navigator.geolocation) {
        navigator.geolocation.getCurrentPosition(fetchWeather, handleLocationError);
    } else {
        handleLocationError();
    }
});

function fetchWeather(position) {
    let lat, lon;
    let isDefaultLocation = false;

    if (position && position.coords) {
        lat = position.coords.latitude;
        lon = position.coords.longitude;
    } else {
        // Default to London if no position is provided
        lat = 51.5074;
        lon = -0.1278;
        isDefaultLocation = true;
    }

    const weatherURL = `https://api.open-meteo.com/v1/forecast?latitude=${lat}&longitude=${lon}&current_weather=true`;
    // Use Nominatim for reverse geocoding
    const geocodeURL = `https://nominatim.openstreetmap.org/reverse?format=jsonv2&lat=${lat}&lon=${lon}`;

    // Fetch weather and location data in parallel
    Promise.all([
        $.ajax({ url: weatherURL, method: 'GET' }),
        isDefaultLocation ? Promise.resolve({ address: { city: 'London' } }) : $.ajax({ url: geocodeURL, method: 'GET' })
    ])
    .then(([weatherData, geocodeData]) => {
        const weather = processWeatherData(weatherData, geocodeData);
        updateWeatherUI(weather);
    })
    .catch(() => {
        const weather = getFallbackWeather(isDefaultLocation ? 'London' : 'Unknown Location');
        updateWeatherUI(weather);
    });
}

function handleLocationError() {
    fetchWeather(null); // Fetch for default location
}

function processWeatherData(weatherData, geocodeData) {
    const { temperature, weathercode } = weatherData.current_weather;
    const { condition, description } = mapWeatherCode(weathercode);

    let locationName = "Unknown Location";
    if (geocodeData && geocodeData.address) {
        locationName = geocodeData.address.city || geocodeData.address.town || geocodeData.address.village || "Current Location";
    }

    return {
        location: locationName,
        temperature: Math.round(temperature),
        description: description,
        condition: condition
    };
}

function mapWeatherCode(code) {
    if (code >= 0 && code <= 1) return { condition: 'sunny', description: 'Clear sky' };
    if (code >= 2 && code <= 3) return { condition: 'cloudy', description: 'Cloudy' };
    if ((code >= 51 && code <= 67) || (code >= 80 && code <= 82)) return { condition: 'rainy', description: 'Rainy' };
    return { condition: 'cloudy', description: 'Moderate Weather' };
}

function getFallbackWeather(location) {
    return {
        location: location,
        temperature: 20,
        description: 'Cloudy',
        condition: 'cloudy'
    };
}

function updateWeatherUI(weather) {
    $('#location').text(weather.location);
    $('#temperature').text(`${weather.temperature}°C`);
    $('#description').text(weather.description);

    const $animationContainer = $('#weather-animation-container');
    $animationContainer.removeClass('weather-sunny weather-cloudy weather-rainy');
    $animationContainer.addClass(`weather-${weather.condition}`);

    $animationContainer.empty();

    switch (weather.condition) {
        case 'sunny':
            $animationContainer.append('<div class="sun"></div>');
            break;
        case 'cloudy':
            $animationContainer.append('<div class="cloud c1"></div><div class="cloud c2"></div>');
            break;
        case 'rainy':
            for (let i = 0; i < 50; i++) {
                const left = Math.random() * 100;
                const delay = Math.random() * 2;
                $animationContainer.append(`<div class="rain-drop" style="left: ${left}%; animation-delay: ${delay}s;"></div>`);
            }
            break;
    }
}

