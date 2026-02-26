<template>
  <div class="page-loading-skeleton">
    <div class="skeleton pulse title"></div>
    <div class="skeleton pulse line"></div>
    <div class="skeleton pulse line"></div>
    <div class="skeleton pulse line short"></div>

    <div class="cards">
      <div class="skeleton pulse card" v-for="index in 3" :key="index"></div>
    </div>
  </div>
</template>

<style scoped>
.page-loading-skeleton {
  padding: 16px;
}

.skeleton {
  background: linear-gradient(90deg, #f2f3f5 25%, #e8eaef 37%, #f2f3f5 63%);
  background-size: 400% 100%;
  border-radius: 8px;
  margin-bottom: 12px;
}

.pulse {
  animation: skeleton-loading 1.2s ease-in-out infinite;
}

.title {
  width: 180px;
  height: 26px;
}

.line {
  width: 100%;
  height: 14px;
}

.short {
  width: 60%;
}

.cards {
  display: grid;
  gap: 12px;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  margin-top: 16px;
}

.card {
  height: 120px;
}

@keyframes skeleton-loading {
  0% {
    background-position: 100% 50%;
  }

  100% {
    background-position: 0 50%;
  }
}
</style>
