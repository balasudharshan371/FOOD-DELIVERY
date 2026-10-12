/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        brand: {
          50: '#fff1f0',
          100: '#ffe1df',
          500: '#ff5200',
          600: '#e23744',
          700: '#c82633',
        }
      }
    },
  },
  plugins: [],
}
