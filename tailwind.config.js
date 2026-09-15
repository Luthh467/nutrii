/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        nutri: {
          green: '#1B5E20',
          lightGreen: '#4CAF50',
          orange: '#E65100',
          amber: '#F57C00',
          bg: '#F8FAF8'
        }
      }
    },
  },
  plugins: [],
}
