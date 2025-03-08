// utils.js
export function navigateTo(vm, path) {
    if (vm.$route.path !== path) {
        vm.$router.push(path);
    }
}