import api from './axios'

export const markAttendance = (data) =>
  api.post('/attendance/mark', data)

export const syncAttendance = (data) =>
  api.post('/attendance/sync', data)

export const getTodayAttendance = (siteId) =>
  api.get(`/attendance/site/${siteId}/today`)