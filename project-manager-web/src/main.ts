import { createApp } from 'vue';
import { createPinia } from 'pinia';
import App from './App.vue';
import { registerFrontendObservabilityReporter } from '@/observability/frontend-events';
import router from './router';

registerFrontendObservabilityReporter();

const app = createApp(App);
app.use(createPinia());
app.use(router);
app.mount('#app');
