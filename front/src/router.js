import Vue from 'vue';
import VueRouter from 'vue-router';
import Top from './components/Top.vue';
import Login from './components/Login.vue';
import UserSettings from './components/UserSettings.vue';
import Error from './components/Error.vue';
import SchoolTop  from './components/school/Top.vue';
import SchoolEdit  from './components/school/Edit.vue';
import ScheduleCreate  from './components/schedule/Create.vue';
import ScheduleDetail  from './components/schedule/ScheduleDetail.vue';
import PrivateSchedule  from './components/schedule/private/PrivateSchedule.vue';
import SchoolSchedule  from './components/schedule/school/SchoolSchedule.vue';

Vue.use(VueRouter);

const routes = [
  { path: '/', component: Top },
  { path: '/login', component: Login },
  { path: '/user-settings', component: UserSettings },
  { path: '/error', component: Error },
  { path: '/school', component: SchoolTop },
  { path: '/edit-school/:school_code', component: SchoolEdit },
  { path: '/edit-schedule/_new', component: ScheduleCreate },
  { path: '/detail-schedule/:schedule_code', component: ScheduleDetail },
  { path: '/private-schedule/:target_year_and_month', component: PrivateSchedule },
  { path: '/school-schedule/:target_year_and_month/school/:school_code', component: SchoolSchedule },
];

const router = new VueRouter({
  mode: 'hash',
  routes,
});

export default router;
