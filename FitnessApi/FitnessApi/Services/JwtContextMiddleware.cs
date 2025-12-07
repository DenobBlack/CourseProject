namespace FitnessApi.Services
{
    public class JwtContextMiddleware
    {
        private readonly RequestDelegate _next;

        public JwtContextMiddleware(RequestDelegate next) => _next = next;

        public async Task InvokeAsync(HttpContext context)
        {
            if (!context.Request.Headers.ContainsKey("Authorization") &&
                context.Items.ContainsKey("UserToken"))
            {
                var token = context.Items["UserToken"]?.ToString();
                if (!string.IsNullOrEmpty(token))
                {
                    context.Request.Headers.Authorization = $"Bearer {token}";
                }
            }
            if (string.IsNullOrEmpty(context.Request.Headers.Authorization))
            {
                var token = context.Session.GetString("UserToken");
                if (!string.IsNullOrEmpty(token))
                    context.Request.Headers.Authorization = $"Bearer {token}";
            }
            await _next(context);
        }
    }

}
