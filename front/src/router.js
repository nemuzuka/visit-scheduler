import Vue from 'vue';
import VueRouter from 'vue-router';
import Top from './components/Top.vue';
import Login from './components/Login.vue';
import UserSettings from './components/UserSettings.vue';
import Error from './components/Error.vue';
import TaskEdit  from './components/task/Edit.vue';
import SchoolTop  from './components/school/Top.vue';
import SchoolEdit  from './components/school/Edit.vue';
import ScheduleCreate  from './components/schedule/Create.vue';
import ScheduleDetail  from './components/schedule/ScheduleDetail.vue';
import PrivateSchedule  from './components/schedule/private/PrivateSchedule.vue';

Vue.use(VueRouter);

const routes = [
  { path: '/', component: Top },
  { path: '/login', component: Login },
  { path: '/user-settings', component: UserSettings },
  { path: '/error', component: Error },
  { path: '/edit-task/:task_code', component: TaskEdit },
  { path: '/school', component: SchoolTop },
  { path: '/edit-school/:school_code', component: SchoolEdit },
  { path: '/edit-schedule/_new', component: ScheduleCreate },
  { path: '/detail-schedule/:schedule_code', component: ScheduleDetail },
  { path: '/private-schedule/:target_year_and_month', component: PrivateSchedule },
];

const router = new VueRouter({
  mode: 'history',
  routes,
});

export default router;
